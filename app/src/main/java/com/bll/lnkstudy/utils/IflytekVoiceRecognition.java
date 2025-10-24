package com.bll.lnkstudy.utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * 科大讯飞语音识别客户端
 * 
 * 功能：通过科大讯飞 WebSocket API 实现实时语音识别
 * 
 * 使用方式：
 * 1. 实例化客户端，传入讯飞平台的 appId、apiKey、apiSecret
 * 2. 调用 startCapture() 开始录音
 * 3. 调用 stopAndSend() 停止录音并发送到服务器识别
 * 4. 通过 Listener 接收识别结果
 * 
 * 录音参数：
 * - 采样率: 16000Hz
 * - 声道: 单声道
 * - 编码: PCM 16bit
 * 
 * @author 科大讯飞语音识别库
 * @version 1.0
 */
public class IflytekVoiceRecognition {
    
    private static final String TAG = "IflytekVoiceRecognition";
    
    /**
     * 识别结果回调接口
     */
    public interface RecognitionListener {
        /**
         * 部分识别结果（实时返回）
         * @param text 当前识别的文本
         */
        void onPartialResult(String text);
        
        /**
         * 最终识别结果
         * @param text 完整的识别文本
         */
        void onFinalResult(String text);
        
        /**
         * 识别错误
         * @param message 错误信息
         */
        void onError(String message);
        
        /**
         * 连接关闭
         */
        void onClose();
    }

    // 讯飞平台配置
    private final String appId;
    private final String apiKey;
    private final String apiSecret;
    private final RecognitionListener listener;

    // WebSocket 相关
    private final OkHttpClient httpClient;
    private WebSocket webSocket;
    
    // 录音相关
    private Thread recordThread;
    private volatile boolean running = false;
    private ByteArrayOutputStream captureBos;
    private byte[] pendingAudio;
    
    // 识别结果聚合
    private final java.util.TreeMap<Integer, String> snToText = new java.util.TreeMap<>();
    
    // 状态管理
    private enum State { IDLE, RECORDING, SENDING }
    private volatile State state = State.IDLE;
    private volatile boolean finalEmitted = false;

    // 讯飞 API 配置
    private static final String HOST = "iat-api.xfyun.cn";
    private static final String WS_URL = "wss://" + HOST + "/v2/iat";
    
    // 录音参数
    private static final int SAMPLE_RATE = 16000;  // 采样率 16kHz
    private static final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;  // 单声道
    private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;  // 16位PCM编码
    private static final int CHUNK_SIZE = 3200;  // 音频分片大小

    /**
     * 构造函数
     * 
     * @param appId 讯飞开放平台的 APPID
     * @param apiKey 讯飞开放平台的 APIKey
     * @param apiSecret 讯飞开放平台的 APISecret
     * @param listener 识别结果监听器
     */
    public IflytekVoiceRecognition(String appId, String apiKey, String apiSecret, RecognitionListener listener) {
        this.appId = appId;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.listener = listener;
        this.httpClient = new OkHttpClient();
    }

    /**
     * 检查是否正在运行（录音或发送中）
     * @return true=正在运行，false=空闲
     */
    public boolean isRunning() {
        return state != State.IDLE;
    }

    /**
     * 检查是否正在录音
     * @return true=正在录音，false=未录音
     */
    public boolean isRecording() {
        return state == State.RECORDING;
    }

    /**
     * 开始录音采集
     * 按下录音按钮时调用，开始采集音频但不发送
     */
    public void startCapture() {
        if (state != State.IDLE) {
            Log.w(TAG, "当前状态不是空闲，无法开始录音");
            return;
        }
        
        // 清理旧资源
        cleanupResources();
        
        state = State.RECORDING;
        running = true;
        captureBos = new ByteArrayOutputStream(64 * 1024);
        recordThread = new Thread(this::doRecordToBuffer, "iflytek-capture");
        recordThread.start();
        
        // 重置识别结果
        snToText.clear();
        finalEmitted = false;
        pendingAudio = null;
        
        Log.i(TAG, "开始录音采集");
    }

    /**
     * 停止录音并发送到服务器识别
     * 松开录音按钮时调用
     */
    public void stopAndSend() {
        if (state != State.RECORDING) {
            if (listener != null) {
                listener.onError("当前未在录音");
            }
            return;
        }
        
        Log.i(TAG, "停止录音，准备发送");
        
        // 停止录音
        running = false;
        try {
            if (recordThread != null) {
                recordThread.join(500);
            }
        } catch (InterruptedException ignore) {}
        
        // 获取录音数据
        pendingAudio = captureBos != null ? captureBos.toByteArray() : new byte[0];
        if (pendingAudio.length == 0) {
            if (listener != null) {
                listener.onError("未采集到音频");
            }
            state = State.IDLE;
            return;
        }
        
        Log.i(TAG, "采集到音频数据: " + pendingAudio.length + " bytes");
        
        // 连接 WebSocket 并发送
        try {
            String authUrl = buildAuthUrl();
            Request request = new Request.Builder().url(authUrl).build();
            state = State.SENDING;
            webSocket = httpClient.newWebSocket(request, new WsHandler());
            finalEmitted = false;
        } catch (Exception e) {
            state = State.IDLE;
            if (listener != null) {
                listener.onError("鉴权失败: " + e.getMessage());
            }
            Log.e(TAG, "鉴权失败", e);
        }
    }

    /**
     * 停止识别（取消）
     */
    public void stop() {
        Log.i(TAG, "停止识别");
        running = false;
        state = State.IDLE;
        
        // 清理资源
        cleanupResources();
    }
    
    /**
     * 释放所有资源
     * 在 Activity onDestroy 或不再需要识别时调用
     */
    public void release() {
        Log.i(TAG, "释放资源");
        stop();
    }
    
    /**
     * 清理所有资源（供内部使用）
     */
    private void cleanupResources() {
        // 关闭 WebSocket
        if (webSocket != null) {
            try {
                webSocket.close(1000, "cleanup");
            } catch (Exception ignore) {}
            webSocket = null;
        }
        
        // 清理录音线程
        if (recordThread != null && recordThread.isAlive()) {
            try {
                recordThread.interrupt();
            } catch (Exception ignore) {}
            recordThread = null;
        }
        
        // 清理音频数据
        if (captureBos != null) {
            try {
                captureBos.reset();
                captureBos.close();
            } catch (Exception ignore) {}
            captureBos = null;
        }
        
        pendingAudio = null;
    }

    /**
     * 构建鉴权 URL
     * 使用 HMAC-SHA256 算法对请求进行签名
     */
    private String buildAuthUrl() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = sdf.format(new Date());
        
        // 构建签名原文
        String signatureOrigin = "host: " + HOST + "\n" +
                "date: " + date + "\n" +
                "GET /v2/iat HTTP/1.1";
        
        // HMAC-SHA256 签名
        String signatureSha = hmacSha256Base64(signatureOrigin, apiSecret);
        
        // 构建 Authorization
        String authorizationOrigin = String.format(
                Locale.US,
                "api_key=\"%s\", algorithm=\"hmac-sha256\", headers=\"host date request-line\", signature=\"%s\"",
                apiKey, signatureSha
        );
        
        String authorization = Base64.encodeToString(
                authorizationOrigin.getBytes(StandardCharsets.UTF_8), 
                Base64.NO_WRAP
        );
        
        // 构建完整 URL
        String url = WS_URL + 
                "?authorization=" + URLEncoder.encode(authorization, "UTF-8") +
                "&date=" + URLEncoder.encode(date, "UTF-8") +
                "&host=" + HOST;
        
        return url;
    }

    /**
     * HMAC-SHA256 签名并 Base64 编码
     */
    private static String hmacSha256Base64(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        Key secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKey);
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeToString(hash, Base64.NO_WRAP);
    }

    /**
     * WebSocket 消息处理器
     */
    private class WsHandler extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            Log.i(TAG, "WebSocket 连接成功");
            IflytekVoiceRecognition.this.webSocket = webSocket;
            
            if (state == State.SENDING) {
                // 发送缓存的音频
                startSendBuffer();
            }
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            try {
                org.json.JSONObject root = new org.json.JSONObject(text);
                int code = root.optInt("code", 0);
                
                if (code != 0) {
                    String msg = root.optString("message", "服务返回错误");
                    if (listener != null) {
                        listener.onError(code + ": " + msg);
                    }
                    Log.e(TAG, "服务错误: " + code + " - " + msg);
                    return;
                }
                
                org.json.JSONObject data = root.optJSONObject("data");
                if (data == null) return;
                
                int status = data.optInt("status", -1);
                org.json.JSONObject result = data.optJSONObject("result");
                
                if (result != null) {
                    // 解析识别结果
                    int sn = result.optInt("sn", snToText.isEmpty() ? 0 : (snToText.lastKey() + 1));
                    String pgs = result.optString("pgs", "");
                    org.json.JSONArray rg = result.optJSONArray("rg");
                    String seg = IflytekResultParser.parse(text);
                    
                    if (seg != null && !seg.isEmpty()) {
                        // 处理动态修正
                        if ("rpl".equals(pgs) && rg != null && rg.length() == 2) {
                            int start = rg.optInt(0);
                            int end = rg.optInt(1);
                            
                            // 删除被替换的片段
                            java.util.ArrayList<Integer> toRemove = new java.util.ArrayList<>();
                            for (Integer k : snToText.keySet()) {
                                if (k >= start && k <= end) {
                                    toRemove.add(k);
                                }
                            }
                            for (Integer k : toRemove) {
                                snToText.remove(k);
                            }
                        }
                        
                        snToText.put(sn, seg);
                        
                        // 组装完整文本
                        StringBuilder all = new StringBuilder();
                        for (String v : snToText.values()) {
                            all.append(v);
                        }
                        
                        // 回调部分结果
                        if (listener != null) {
                            listener.onPartialResult(all.toString());
                        }
                        
                        // 检查是否结束
                        if (status == 2 || data.optBoolean("ls", false)) {
                            if (!finalEmitted && listener != null) {
                                finalEmitted = true;
                                listener.onFinalResult(all.toString());
                                Log.i(TAG, "识别完成: " + all.toString());
                            }
                            snToText.clear();
                            try {
                                webSocket.close(1000, "done");
                            } catch (Exception ignore) {}
                            IflytekVoiceRecognition.this.webSocket = null;
                            state = State.IDLE;
                        }
                    }
                } else if (status == 2) {
                    // 没有识别结果，直接结束
                    if (!finalEmitted && listener != null) {
                        finalEmitted = true;
                        listener.onFinalResult("");
                    }
                    try {
                        webSocket.close(1000, "done");
                    } catch (Exception ignore) {}
                    IflytekVoiceRecognition.this.webSocket = null;
                    state = State.IDLE;
                }
            } catch (Exception e) {
                if (listener != null) {
                    listener.onError("解析失败: " + e.getMessage());
                }
                Log.e(TAG, "解析响应失败", e);
                // 解析失败时也要清理状态
                state = State.IDLE;
                IflytekVoiceRecognition.this.webSocket = null;
            }
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
            Log.e(TAG, "WebSocket 失败", t);
            running = false;
            state = State.IDLE;
            
            // 清理 WebSocket 引用
            IflytekVoiceRecognition.this.webSocket = null;
            
            if (listener != null) {
                listener.onError("连接失败: " + t.getMessage());
            }
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            Log.i(TAG, "WebSocket 关闭: " + code + " - " + reason);
            running = false;
            state = State.IDLE;
            finalEmitted = false;
            
            // 清理 WebSocket 引用
            IflytekVoiceRecognition.this.webSocket = null;
            
            if (listener != null) {
                listener.onClose();
            }
        }
    }

    /**
     * 录音到缓冲区
     */
    private void doRecordToBuffer() {
        int minBuf = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL, ENCODING);
        AudioRecord recorder = new AudioRecord(
                MediaRecorder.AudioSource.MIC, 
                SAMPLE_RATE, 
                CHANNEL, 
                ENCODING, 
                minBuf
        );
        
        byte[] buffer = new byte[CHUNK_SIZE];
        
        try {
            recorder.startRecording();
            Log.i(TAG, "录音开始");
            
            while (running) {
                int read = recorder.read(buffer, 0, buffer.length);
                if (read > 0) {
                    captureBos.write(buffer, 0, read);
                }
            }
            
            Log.i(TAG, "录音结束，共采集: " + captureBos.size() + " bytes");
        } catch (Exception e) {
            if (listener != null) {
                listener.onError("录音失败: " + e.getMessage());
            }
            Log.e(TAG, "录音失败", e);
        } finally {
            try {
                recorder.stop();
            } catch (Exception ignore) {}
            try {
                recorder.release();
            } catch (Exception ignore) {}
        }
    }

    /**
     * 发送缓存的音频数据
     */
    private void startSendBuffer() {
        Log.i(TAG, "开始发送音频数据");
        
        new Thread(() -> {
            try {
                WebSocket ws = this.webSocket;
                if (ws == null) {
                    if (listener != null) {
                        listener.onError("连接未建立");
                    }
                    state = State.IDLE;
                    return;
                }
                
                if (pendingAudio == null || pendingAudio.length == 0) {
                    if (listener != null) {
                        listener.onError("无音频数据");
                    }
                    state = State.IDLE;
                    return;
                }
                
                int idx = 0;
                int total = pendingAudio.length;
                
                // 发送首帧（包含配置参数）
                int firstLen = Math.min(CHUNK_SIZE, total);
                String first = JsonBuilder.firstFrame(appId, pendingAudio, firstLen);
                boolean ok = ws.send(first);
                
                if (!ok) {
                    if (listener != null) {
                        listener.onError("发送首包失败");
                    }
                    state = State.IDLE;
                    return;
                }
                
                Log.i(TAG, "首帧发送成功");
                idx += firstLen;
                
                // 发送音频数据帧
                while (idx < total) {
                    int len = Math.min(CHUNK_SIZE, total - idx);
                    byte[] chunk = copyOfRange(pendingAudio, idx, idx + len);
                    String frame = JsonBuilder.audioFrame(chunk, len);
                    
                    if (!ws.send(frame)) {
                        if (listener != null) {
                            listener.onError("发送音频失败");
                        }
                        break;
                    }
                    
                    idx += len;
                    Thread.sleep(40);  // 控制发送速度
                }
                
                // 发送结束帧
                ws.send(JsonBuilder.lastFrame());
                Log.i(TAG, "音频发送完成，等待识别结果");
                
            } catch (Exception e) {
                if (listener != null) {
                    listener.onError("发送失败: " + e.getMessage());
                }
                Log.e(TAG, "发送音频失败", e);
            }
        }, "iflytek-send").start();
    }

    /**
     * 字节数组工具方法
     */
    private static byte[] copyOfRange(byte[] src, int from, int to) {
        int len = Math.max(0, to - from);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(len);
        bos.write(src, from, len);
        return bos.toByteArray();
    }

    /**
     * JSON 帧构建器
     */
    static class JsonBuilder {
        /**
         * 构建首帧（包含业务参数）
         */
        static String firstFrame(String appId, byte[] audio, int len) {
            String base64 = len > 0 ? 
                    Base64.encodeToString(copyOf(audio, len), Base64.NO_WRAP) : "";
            
            // 业务参数配置
            return "{\n" +
                    "  \"common\":{\"app_id\":\"" + appId + "\"},\n" +
                    "  \"business\":{" +
                    "\"language\":\"zh_cn\"," +      // 中文
                    "\"domain\":\"iat\"," +          // 通用领域
                    "\"accent\":\"mandarin\"," +     // 普通话
                    "\"vad_eos\":1000," +            // 静音检测时长 1秒
                    "\"dwa\":\"wpgs\"," +            // 开启动态修正
                    "\"ptt\":1" +                    // 开启标点符号
                    "},\n" +
                    "  \"data\":{" +
                    "\"status\":0," +
                    "\"format\":\"audio/L16;rate=16000\"," +
                    "\"encoding\":\"raw\"," +
                    "\"audio\":\"" + base64 + "\"" +
                    "}\n" +
                    "}";
        }
        
        /**
         * 构建音频数据帧
         */
        static String audioFrame(byte[] data, int len) {
            String base64 = Base64.encodeToString(copyOf(data, len), Base64.NO_WRAP);
            return "{\n" +
                    "  \"data\":{" +
                    "\"status\":1," +
                    "\"format\":\"audio/L16;rate=16000\"," +
                    "\"encoding\":\"raw\"," +
                    "\"audio\":\"" + base64 + "\"" +
                    "}\n" +
                    "}";
        }
        
        /**
         * 构建结束帧
         */
        static String lastFrame() {
            return "{\n" +
                    "  \"data\":{" +
                    "\"status\":2," +
                    "\"audio\":\"\"," +
                    "\"encoding\":\"raw\"," +
                    "\"format\":\"audio/L16;rate=16000\"" +
                    "}\n" +
                    "}";
        }
        
        private static byte[] copyOf(byte[] src, int len) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(len);
            bos.write(src, 0, len);
            return bos.toByteArray();
        }
    }

    /**
     * 讯飞识别结果解析器
     */
    static class IflytekResultParser {
        /**
         * 解析识别结果 JSON
         * @param json 服务器返回的 JSON 字符串
         * @return 识别的文本
         */
        static String parse(String json) {
            try {
                org.json.JSONObject obj = new org.json.JSONObject(json);
                if (!obj.has("data")) return "";
                
                org.json.JSONObject data = obj.getJSONObject("data");
                if (!data.has("result")) return "";
                
                org.json.JSONObject result = data.getJSONObject("result");
                if (!result.has("ws")) return "";
                
                org.json.JSONArray ws = result.getJSONArray("ws");
                StringBuilder sb = new StringBuilder();
                
                for (int i = 0; i < ws.length(); i++) {
                    org.json.JSONArray cw = ws.getJSONObject(i).getJSONArray("cw");
                    if (cw.length() > 0) {
                        sb.append(cw.getJSONObject(0).optString("w", ""));
                    }
                }
                
                return sb.toString();
            } catch (Exception e) {
                Log.e(TAG, "解析识别结果失败", e);
                return "";
            }
        }
    }
}


