package com.bll.lnkstudy.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.EinkPWInterface;
import android.view.MotionEvent;
import android.view.PWDrawObjectHandler;
import android.view.PWInputPoint;
import android.view.RectArray;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

public class CustomImageView extends ImageView implements EinkPWInterface {

    public CustomImageView(Context context) {
        super(context);
    }

    @Override
    public void OSULOG(String s, boolean b) {

    }

    @Override
    public int getSeqId() {
        return 0;
    }

    @Override
    public void enableTouchDispatch(int i) {

    }

    @Override
    public int getTouchDispatch() {
        return 0;
    }

    @Override
    public void disableTouchInput(boolean b) {

    }

    @Override
    public void enablePenClick(boolean b) {

    }

    @Override
    public void enablePenClick(boolean b, PWEventClickCallback pwEventClickCallback) {

    }

    @Override
    public void setPenClickMoveOutMM(float v) {

    }

    @Override
    public void setOneWordDelayMs(int i) {

    }

    @Override
    public Canvas lockCanvasFast() {
        return null;
    }

    @Override
    public void postCanvasFast(Canvas canvas, Rect rect, boolean b) {

    }

    @Override
    public void postCanvas(Canvas canvas, boolean b) {

    }

    @Override
    public void syncSurfaceDisplay() {

    }

    @Override
    public void setEinkFCMode(boolean b) {

    }

    @Override
    public void setLockUIWhenWriting(boolean b) {

    }

    @Override
    public boolean addOneDirtyRect(Rect rect, boolean b, int i) {
        return false;
    }

    @Override
    public boolean addDirtyRects(RectArray rectArray, int i) {
        return false;
    }

    @Override
    public void invalidateHost(Rect rect) {

    }

    @Override
    public ArrayList<Rect> getHostViewDirty() {
        return null;
    }

    @Override
    public void setEinkPWMode(short i) {

    }

    @Override
    public void setEinkEraseMode(short i) {

    }

    @Override
    public void setEinkA2Gate(int i) {

    }

    @Override
    public void setEinkEraseA2Gate(int i) {

    }

    @Override
    public void setGrayEraseRect(Rect rect) {

    }

    @Override
    public void setHostView(View view) {

    }

    @Override
    public void onHostViewEnableChanged(boolean b) {

    }

    @Override
    public void onHostViewVisibleChanged(boolean b) {

    }

    @Override
    public void onHostViewAttachedToWindow() {

    }

    @Override
    public void onHostViewDetachedFromWindow() {

    }

    @Override
    public View getHostView() {
        return null;
    }

    @Override
    public Rect getWinFrame() {
        return null;
    }

    @Override
    public void setScrollView(View view) {

    }

    @Override
    public void setScrollRect(Rect rect) {

    }

    @Override
    public void setPwViewScrollRect(Rect rect) {

    }

    @Override
    public Rect getScrollRect() {
        return null;
    }

    @Override
    public void setLoadFilePath(String s, boolean b) {

    }

    @Override
    public void setLoadFilePathWithListener(String s, boolean b, PWLSBitmapListener pwlsBitmapListener) {

    }

    @Override
    public void setLoadFilePathWithListener(String s, boolean b, PWLSBitmapListenerX pwlsBitmapListenerX) {

    }

    @Override
    public void setPreloadFilePath(String s) {

    }

    @Override
    public boolean freeCachePWBitmapFilePath(String s) {
        return false;
    }

    @Override
    public boolean freeCachePWBitmapFilePath(String s, boolean b) {
        return false;
    }

    @Override
    public void freeAllPWBitmapCache(boolean b) {

    }

    @Override
    public void reLoadFilePath(String s, PWLSBitmapListener pwlsBitmapListener) {

    }

    @Override
    public void reLoadFilePath(String s, PWLSBitmapListenerX pwlsBitmapListenerX) {

    }

    @Override
    public String getPWTouchFilePath() {
        return null;
    }

    @Override
    public String getPWBitmapFilePath() {
        return null;
    }

    @Override
    public void setNeedSaveFiles(boolean b) {

    }

    @Override
    public boolean getNeedSaveFiles() {
        return false;
    }

    @Override
    public void disablePenErase(boolean b) {

    }

    @Override
    public boolean isPenEraseDisable() {
        return false;
    }

    @Override
    public void enablePenCapEraseTrack(boolean b) {

    }

    @Override
    public boolean isPenCapEraseTrackEnabled() {
        return false;
    }

    @Override
    public void disablePenEraseFloatFlower(boolean b) {

    }

    @Override
    public boolean isPenEraseFloatFlowerDIsabled() {
        return false;
    }

    @Override
    public void setPenEraseFloatFlowerBitmap(Bitmap bitmap) {

    }

    @Override
    public Bitmap getPenEraseFloatFlowerBitmap() {
        return null;
    }

    @Override
    public void enablePenCapEraseChoice(boolean b) {

    }

    @Override
    public boolean isPenCapEraseChoiceEnabled() {
        return false;
    }

    @Override
    public void setUserErase(boolean b) {

    }

    @Override
    public boolean isUserEraseSet() {
        return false;
    }

    @Override
    public void setUserRepaint(boolean b) {

    }

    @Override
    public boolean isUserRepaintSet() {
        return false;
    }

    @Override
    public void dispalyPWBitmapOnGone(boolean b) {

    }

    @Override
    public float setPenSettingWidthExt(int i) {
        return 0;
    }

    @Override
    public int setPenSettingWidth(int i) {
        return 0;
    }

    @Override
    public int getPenSettingWidth() {
        return 0;
    }

    @Override
    public float getPenStdWidth() {
        return 0;
    }

    @Override
    public void setPenStdWidth(float v) {

    }

    @Override
    public float setPenEraseWidthExt(int i) {
        return 0;
    }

    @Override
    public int setPenEraseWidth(int i) {
        return 0;
    }

    @Override
    public int getPenEraseWidth() {
        return 0;
    }

    @Override
    public float getPenStdEraseWidth() {
        return 0;
    }

    @Override
    public void setPenStdEraseWidth(float v) {

    }

    @Override
    public void setPenWithMapArray(float[] floats) {

    }

    @Override
    public void setPenEraseWithMapArray(float[] floats) {

    }

    @Override
    public int setPenColor(int i) {
        return 0;
    }

    @Override
    public int getPenColor() {
        return 0;
    }

    @Override
    public float getPenAdjRate() {
        return 0;
    }

    @Override
    public void registerDrawObjectHandler(PWDrawObjectHandler pwDrawObjectHandler) {

    }

    @Override
    public void unRegisterDrawObjectHandler(PWDrawObjectHandler pwDrawObjectHandler) {

    }

    @Override
    public void setDrawEventListener(PWDrawEvent pwDrawEvent) {

    }

    @Override
    public void setDrawEventListener(PWDrawEventWithPoint pwDrawEventWithPoint) {

    }

    @Override
    public void setEraseEventListener(PWEraseEventListener pwEraseEventListener) {

    }

    @Override
    public void setPWTouchEventListener(PWTouchEventListener pwTouchEventListener) {

    }

    @Override
    public void clearContent(Rect rect, boolean b, boolean b1) {

    }

    @Override
    public void clearContent(Rect rect, boolean b, boolean b1, boolean b2) {

    }

    @Override
    public boolean clearContentX(boolean b) {
        return false;
    }

    @Override
    public void clearPWRect(Rect rect) {

    }

    @Override
    public void setPWEnabled(boolean b) {

    }

    @Override
    public void disablePWInput(boolean b) {

    }

    @Override
    public Bitmap getPureWriteBitmap() {
        return null;
    }

    @Override
    public Bitmap getPureWriteBitmapRef() {
        return null;
    }

    @Override
    public void freePureWriteBitmap(Bitmap bitmap) {

    }

    @Override
    public Bitmap getCachePWBitmapFilePathRef(String s, boolean b) {
        return null;
    }

    @Override
    public void setPWBitmap(Bitmap bitmap, Rect rect, Rect rect1, boolean b) {

    }

    @Override
    public void setPWBitmap(Bitmap bitmap) {

    }

    @Override
    public void saveBitmap(boolean b, PWSaveBitmapListener pwSaveBitmapListener) {

    }

    @Override
    public void saveBitmap(PWLSBitmapListener pwlsBitmapListener) {

    }

    @Override
    public void saveBitmap(PWLSBitmapListenerX pwlsBitmapListenerX) {

    }

    @Override
    public void saveBitmapAndWaitDone(long l) {

    }

    @Override
    public void setFingerWritable(boolean b) {

    }

    @Override
    public boolean getFingerWritable() {
        return false;
    }

    @Override
    public void setFingerErasable(boolean b) {

    }

    @Override
    public boolean getFingerErasable() {
        return false;
    }

    @Override
    public void setWifiCasting(boolean b) {

    }

    @Override
    public boolean isAtWifiCasting() {
        return false;
    }

    @Override
    public void setDrawObjectType(int i) {

    }

    @Override
    public int getDrawObjectType() {
        return 0;
    }

    @Override
    public PWDrawObjectHandler getCurDrawObjectHandler() {
        return null;
    }

    @Override
    public Canvas getFloatBitmapCanvas() {
        return null;
    }

    @Override
    public void postDrawObjectCallback(PWDrawObjectHandler pwDrawObjectHandler, Object o, int i) {

    }

    @Override
    public void setDrawObjectPaintFill(boolean b) {

    }

    @Override
    public boolean getDrawObjectPaintFill() {
        return false;
    }

    @Override
    public void setDrawObjectPaintAntiAlias(int i) {

    }

    @Override
    public int getDrawObjectPaintAntiAlias() {
        return 0;
    }

    @Override
    public int addSelectObj(int i, Bitmap bitmap, Rect rect, Object o, PWSelectObjStatusListener pwSelectObjStatusListener, boolean b) {
        return 0;
    }

    @Override
    public void removeSelectObj(int i, Object o) {

    }

    @Override
    public void setBgMatts(BackGroundMatts backGroundMatts) {

    }

    @Override
    public BackGroundMatts getBgMatts() {
        return null;
    }

    @Override
    public void registerBgMatts(BackGroundMatts backGroundMatts) {

    }

    @Override
    public void setBackGroupMatts(int i, int i1) {

    }

    @Override
    public int getBackGroundMattsType() {
        return 0;
    }

    @Override
    public int getAvailableUndo() {
        return 0;
    }

    @Override
    public boolean unDo() {
        return false;
    }

    @Override
    public boolean unDo(boolean b) {
        return false;
    }

    @Override
    public boolean unDoByXY(int i, int i1) {
        return false;
    }

    @Override
    public int getAvailableRedo() {
        return 0;
    }

    @Override
    public boolean reDo() {
        return false;
    }

    @Override
    public boolean reDo(int i, int i1) {
        return false;
    }

    @Override
    public ArrayList<PWInputPoint> getStepPointArray(int i) {
        return null;
    }

    @Override
    public int getAvailableRepaintStep() {
        return 0;
    }

    @Override
    public boolean repaintStep(int i, int i1, int i2, int i3, RepaintListener repaintListener) {
        return false;
    }

    @Override
    public void stopRepaint() {

    }

    @Override
    public boolean isRepainting() {
        return false;
    }

    @Override
    public boolean isCurrentWriting() {
        return false;
    }

    @Override
    public boolean repaintOnePoint() {
        return false;
    }

    @Override
    public boolean emulateLastTouchInput() {
        return false;
    }

    @Override
    public void addUnWriteRect(Rect rect) {

    }

    @Override
    public ArrayList<Rect> getUnWriteRectList() {
        return null;
    }

    @Override
    public void clearUnWriteRectList() {

    }

    @Override
    public Rect getAllPWListDrawRect() {
        return null;
    }

    @Override
    public Rect getCurPWListDrawRect() {
        return null;
    }

    @Override
    public void addGrayRect(Rect rect) {

    }

    @Override
    public ArrayList<Rect> getGrayRectList() {
        return null;
    }

    @Override
    public void clearGrayRectList() {

    }

    @Override
    public boolean addWritableRects(ArrayList<Rect> arrayList) {
        return false;
    }

    @Override
    public boolean rmAllWritableRects() {
        return false;
    }

    @Override
    public void addOnTopView(View view) {

    }

    @Override
    public boolean removeOnTopViewX(View view) {
        return false;
    }

    @Override
    public void removeOnTopView(View view) {

    }

    @Override
    public boolean sendBackEvent(MotionEvent motionEvent, boolean b) {
        return false;
    }

    @Override
    public boolean sendBackEventXY(MotionEvent motionEvent, int i, int i1, boolean b) {
        return false;
    }

    @Override
    public boolean sendBackEventAXYP(MotionEvent motionEvent, int i, int i1, int i2, int i3, int i4, float v, boolean b) {
        return false;
    }

    @Override
    public boolean sendBackEventAXYPTilt(MotionEvent motionEvent, int i, int i1, int i2, int i3, int i4, float v, int i5, int i6, boolean b) {
        return false;
    }

    @Override
    public boolean setCurrentPWBmpAsSleepBmp() {
        return false;
    }

    @Override
    public boolean setCurrentPWBmpAsSleepBmp(Bitmap bitmap) {
        return false;
    }

    @Override
    public void setWritableWhenImeInput(boolean b) {

    }

    @Override
    public void setPenType(int i) {

    }

    @Override
    public void setPenType(int i, boolean b) {

    }

    @Override
    public int getPenType() {
        return 0;
    }

    @Override
    public boolean isPenTypeValid(int i) {
        return false;
    }

    @Override
    public void setCustomerPenType(int i, float v, int i1, float v1) {

    }

    @Override
    public void setSelectPixelDistance(int i) {

    }

    @Override
    public int getSelectPixelDistance() {
        return 0;
    }

    @Override
    public void registerPointWidthCallBack(int i, OnUpdatePointWidthCallBack onUpdatePointWidthCallBack) {

    }

    @Override
    public void unregisterPointWidthCallBack(int i) {

    }

    @Override
    public boolean isErasing() {
        return false;
    }

    @Override
    public void updateSufaceVisible(boolean b) {

    }

    @Override
    public void setPWBitmapInVisible(boolean b) {

    }

    @Override
    public void DrawPWBitmapInternal(Canvas canvas) {

    }

    @Override
    public void onWindowChanged() {

    }

    @Override
    public void onWindowDied() {

    }

    @Override
    public void setVriSurface(Surface surface) {

    }

    @Override
    public Bitmap getFloatBitmap() {
        return null;
    }

    @Override
    public void dumpPointLists(String s) {

    }

    @Override
    public void setFlyBlossEnable(boolean b) {

    }

    @Override
    public Paint getDrawingPaint() {
        return null;
    }

    @Override
    public Paint getErasingPaint() {
        return null;
    }

    @Override
    public Paint getUndoPaint() {
        return null;
    }

    @Override
    public void setCpuFreqLevel(int i) {

    }

    @Override
    public void restoreCpuFreq() {

    }

    @Override
    public void setCurSelObjInVisible(boolean b) {

    }

    @Override
    public void waitObjWriterDone(long l) {

    }

    @Override
    public void getUndrawPWRect(Rect rect) {

    }
}
