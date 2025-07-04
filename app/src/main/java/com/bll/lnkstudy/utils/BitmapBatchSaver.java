package com.bll.lnkstudy.utils;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 500ms自动保存
 */
public class BitmapBatchSaver {
    private final ExecutorService executor;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Map<String, Bitmap> pendingMap = new HashMap<>();
    private final AtomicInteger activeCount = new AtomicInteger(0);
    private final long idleThreshold = 500;
    private Runnable runnable;
    private boolean isAccomplish=true;

    public interface SaveCallback {
        void onSaved(String path);
        void onError(String path, Exception e);
    }

    public BitmapBatchSaver(int threadCount) {
        this.executor = Executors.newFixedThreadPool(threadCount);
    }

    public void submitBitmap(Bitmap bitmap, String path, SaveCallback callback) {
        synchronized (pendingMap) {
            pendingMap.put(path, bitmap);
            resetTimer();
        }
    }

    private void resetTimer() {
        if (runnable != null) {
            mainHandler.removeCallbacks(runnable);
        }
        runnable = this::processPendingTasks;
        mainHandler.postDelayed(runnable, idleThreshold);
    }

    private void processPendingTasks() {
        synchronized (pendingMap) {
            if (pendingMap.isEmpty()) return;
            isAccomplish=false;
            Map<String, Bitmap> currentBatch = new HashMap<>(pendingMap);
            pendingMap.clear();
            activeCount.addAndGet(currentBatch.size());

            for (Map.Entry<String, Bitmap> entry : currentBatch.entrySet()) {
                executor.execute(() -> saveToFile(entry.getKey(), entry.getValue()));
            }
        }
    }

    private void saveToFile(String path, Bitmap bitmap) {
        try {
            File file = new File(path);
            File parent = file.getParentFile();
            if (!parent.exists()) parent.mkdirs();
            if (!file.exists())file.createNewFile();
            try (FileOutputStream fos = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                mainHandler.post(() -> {
                    if (activeCount.decrementAndGet() == 0) {
                        isAccomplish=true;
                    }
                });
            }
        } catch (Exception e) {
            mainHandler.post(() -> {
            });
        }
    }

    public void shutdown() {
        executor.shutdown();
    }

    public boolean isAccomplished(){
        return isAccomplish;
    }
}
