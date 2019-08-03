package ru.skillbranch.devintensive.extensions

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.activity_profile.*
import ru.skillbranch.devintensive.R
import android.opengl.ETC1.getHeight



fun Activity.hideKeyboard(){
//    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//    imm.hideSoftInputFromWindow(findViewById(android.R.id.content).getWindowToken(), 0);
//    try {
//        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
//    } catch (ignored: RuntimeException) { }
//    return false
    val inputManager:InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.SHOW_FORCED)
}



fun Activity.isKeyboardOpen():Boolean{
    val rootView = ll_act
    var rr=Rect()
    rootView.getWindowVisibleDisplayFrame(rr)
    val screenHeight = rootView.getRootView().getHeight()
//    Log.d("M_Activity","getWindowVisibleDisplayFrame: ${screenHeight}")
//    Log.d("M_Activity","getWindowVisibleDisplayFrame: ${rr.top} ${rr.bottom} ${rr.left} ${rr.right}")
//    rootView.getClipBounds(rr)
//    Log.d("M_Activity","getClipBounds: ${rr.top} ${rr.bottom} ${rr.left} ${rr.right}")
//    rootView.getDrawingRect(rr)
//    Log.d("M_Activity","getDrawingRectA: ${rr.top} ${rr.bottom} ${rr.left} ${rr.right}")
//    rootView.getHitRect(rr)
//    Log.d("M_Activity","getHitRect: ${rr.top} ${rr.bottom} ${rr.left} ${rr.right}")
//    rootView.getGlobalVisibleRect(rr)
//    Log.d("M_Activity","getGlobalVisibleRect: ${rr.top} ${rr.bottom} ${rr.left} ${rr.right}")
    if (screenHeight-rr.bottom>screenHeight*0.15) return true else return false

//    return rootView.height!=rr.bottom-rr.top
}

fun Activity.isKeyboardClosed():Boolean{
    val rootView = ll_act
    var rr=Rect()
    rootView.getWindowVisibleDisplayFrame(rr)
    val screenHeight = rootView.getRootView().getHeight()
    if (screenHeight-rr.bottom<screenHeight*0.15) return true else return false
}