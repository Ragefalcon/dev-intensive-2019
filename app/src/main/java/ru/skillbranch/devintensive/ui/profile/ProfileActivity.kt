package ru.skillbranch.devintensive.ui.profile

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_profile.*
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.hideKeyboard
import ru.skillbranch.devintensive.extensions.isKeyboardClosed
import ru.skillbranch.devintensive.extensions.isKeyboardOpen
import ru.skillbranch.devintensive.models.Bender
import ru.skillbranch.devintensive.models.Profile
import ru.skillbranch.devintensive.viewmodels.ProfileViewModel

class ProfileActivity : AppCompatActivity() { //, View.OnClickListener
//    lateinit var benderImage: ImageView
//    lateinit var textTxt: TextView
//    lateinit var messageEt: EditText
//    lateinit var sendBtn: ImageView
//    lateinit var benderObj: Bender

    companion object {
        const val IS_EDIT_MODE = "IS_EDIT_MODE"
    }


    private lateinit var viewModel: ProfileViewModel
    var isEditMode = false
    lateinit var viewFields: Map<String, TextView>

    /**
     * Вызывается при первом создании или перезапуске Activity
     *
     * здесь задаётся внешний вид активности (UI) через метод setContentView()
     * инициализируются преставления
     * представления связываются с необходимыми данными и ресурксами
     * связываются данные со списками
     *
     * Этот метод также предоставляет Bundle, содержащий ранее сохраненное
     * состояние Activity, если оно было.
     *
     * Всегда сопровождается вызовом onStart().
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initViews(savedInstanceState)
        initViewModel()

        Log.d("M_ProfileActivity","onCreate")
//        // benderImage = findViewById(R.id.iv_bender)
//        benderImage = iv_bender
//        textTxt = tv_text
//        messageEt = et_message
//        sendBtn = iv_send
//
//        messageEt.setOnEditorActionListener { v, actionId, event ->
//            if(actionId == EditorInfo.IME_ACTION_DONE){
//                sendBtn.callOnClick()
//                Log.d("M_MainActivity", "Done button is pressed!!!!")
//                true
//            } else {
//                false
//            }
//        }
//
//        val status = savedInstanceState?.getString("STATUS") ?: Bender.Status.NORMAL.name
//        val question = savedInstanceState?.getString("QUESTION") ?: Bender.Question.NAME.name
//        benderObj = Bender(Bender.Status.valueOf(status),Bender.Question.valueOf(question))
//
//        Log.d("M_MainActivity", "onCreate $status $question")
//        val (r,g,b) = benderObj.status.color
//        benderImage.setColorFilter(Color.rgb(r,g,b),PorterDuff.Mode.MULTIPLY)
//
//        textTxt.text = benderObj.askQuestion()
//
//        sendBtn.setOnClickListener(this)
    }

    private fun initViewModel(){
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        viewModel.getProfileData().observe(this, Observer { uploadUI(it) })
        viewModel.getTheme().observe(this, Observer { updateTheme(it) })
    }

    private fun updateTheme(mode: Int) {
        Log.d("M_ProfileActivity","updateTheme $mode")
        delegate.setLocalNightMode(mode)
    }

    private fun uploadUI(profile: Profile) {
        profile.toMap().also {
            for ((k,v) in viewFields){
                v.text= it[k].toString()
            }
        }

    }

    private fun saveProfileInfo(){
        Profile(
            firstName = et_first_name.text.toString(),
            lastName = et_last_name.text.toString(),
            about = et_about.text.toString(),
            repository = et_repository.text.toString()
        ).apply {
            Log.d("M_ProfileActivity","Перед viewModel.saveProfileData")
            viewModel.saveProfileData(this)
        }
    }
    @SuppressLint("ResourceType")
    private fun initViews(savedInstanceState: Bundle?) {

        viewFields = mapOf(
            "nickName" to tv_nick_name,
            "rank" to tv_rank,
            "firstName" to et_first_name,
            "lastName" to et_last_name,
            "about" to et_about,
            "repository" to et_repository,
            "rating" to tv_rating,
            "respect" to tv_respect
        )
        isEditMode = savedInstanceState?.getBoolean(IS_EDIT_MODE, false) ?: false
        showCurrentMode(isEditMode)

        btn_edit.setOnClickListener {
            if (isEditMode){
                saveProfileInfo()
//                iv_avatar.setBorderColor(Color.RED)
//                iv_avatar.setBorderColor("#2196F3")
            }   else    {
//                iv_avatar.setBorderColor(Color.BLUE)
//                iv_avatar.setBorderColor("#FC4C4C")
            }
            isEditMode = !isEditMode
            showCurrentMode(isEditMode)
        }

        btn_switch_theme.setOnClickListener {
            viewModel.switchTheme()
        }
//        btn_edit.setOnClickListener(object : View.OnClickListener{
//            override fun onClick(v: View?) {
//                isEditMode = !isEditMode
//                showCurrentMode(isEditMode)
//            }
//        })
//
//        btn_edit.setOnClickListener(View.OnClickListener {
//            isEditMode = !isEditMode
//            showCurrentMode(isEditMode)
//        })
    }

    private fun showCurrentMode(isEdit: Boolean) {
        val info = viewFields.filter { setOf("firstName","lastName","about","repository").contains(it.key) }
        for ((_,v) in info) {
            v as EditText
            v.isFocusable = isEdit
            v.isFocusableInTouchMode = isEdit
            v.isEnabled = isEdit
            v.background.alpha = if (isEdit) 255 else 0
        }

        ic_eye.visibility = if (isEdit) View.GONE else View.VISIBLE
        wr_about.isCounterEnabled = isEdit

        with(btn_edit){
            val filter: ColorFilter? = if (isEdit){
                PorterDuffColorFilter(
                    resources.getColor(R.color.color_accent, theme),
                    PorterDuff.Mode.SRC_IN
                )
            } else {
                null
            }

            val icon = if (isEdit) {
                resources.getDrawable(R.drawable.ic_save_black_24dp, theme)
            }   else    {
                resources.getDrawable(R.drawable.ic_edit_black_24dp, theme)
            }

            background.colorFilter = filter
            setImageDrawable(icon)
        }

    }

    /**
     * Если Activity возвращается в приоритетный режим после вызова onStop(),
     * то в этом случае вызывается метод onRestart().
     * Т.е. вызывается после того, как Activity была осатновлена и снова была запущена пользователем.
     * Всегда сопровождается вызовом метода onStart().
     *
     * используется для специальных действий, которые должны выполняться только при повторном запуске Activity
     */
    override fun onRestart() {
        super.onRestart()
        Log.d("M_MainActivity", "onRestart")
    }

    /**
     * При вызове onStart() окно еще не видно пользователю, но вскоре будет видно.
     * Вызывается непосредственно перед тем, как активность становится видимой пользователю.
     *
     * Чтение из базы данных
     * Запуск сложной анимации
     * Запуск потоков, отслеживания показаний датчиков, запросов к GPS, таймеров, сервисов или других процессов,
     * которые нужны исключительно для обновления пользовательского интерфейса
     *
     * Затем следует onResume(), если Activity выходит на передний план.
     */
    override fun onStart() {
        super.onStart()
        Log.d("M_MainActivity", "onStart")
    }

    /**
     * Вызывается, когда Activity начнет взаимодействовать с пользователем.
     *
     * Запуск воспроизведения анимации, аудио и видео
     * регистрация любых BroadcastReceiver или других процессов, которые вы освободили/приостановили в onPause()
     * ыыполнение любых других инициализаций, которые должны происходить, когда Activity вновь активна (камера).
     *
     * Тут должен быть максимально легкий и быстрый код чтобы приложение оставалось отзывчивым
     */
    override fun onResume() {
        super.onResume()
        Log.d("M_MainActivity", "onResume")
    }

    /**
     * Метод onPause() вызывается после сворачивания текущей активности или перехода к новому.
     * От onPause() можно перейти к вызову либо onResume(), либо onStop().
     *
     * остановка анимации, аудио и видео
     * сохранение состояния ползовательского ввода (легкие процессы)
     * сохранение в DB если данные должны быть доступны в новой Activity
     * остановка сервисов, подписок, BroadcastReceiver
     *
     * Тут должен быть максильно легкий и быстрый код чтобы приложение оставалось отзывчивым
     */
    override fun onPause() {
        super.onPause()
        Log.d("M_MainActivity", "onPause")
    }

    /**
     * Метод onStop() вызывается, когда Activity становится невидимым для пользователя.
     * Это может произойти при ее уничтожении, или если была запущена другая Activity (существующая или новая),
     * перекрывшая окно текущей Activity.
     *
     * запись в базу данных
     * приостановка сложной анимации
     * приостановка потоков, отслеживания показаний датчиков, запросов к GPS, таймеров, сервисов или других процессов,
     * которые нужны исключительно для обновления пользовательского интерфейса
     *
     * Не вызывается прп вызове метода finish() у Activity
     */
    override fun onStop() {
        super.onStop()
        Log.d("M_MainActivity", "onStop")
    }

    /**
     * Метод вызывается по окончании работы Activity, при вызове метода finish() или в случае,
     * когда система уничтожает этот экземпляр активности для освобождения ресурсов.
     */
    override fun onDestroy() {
        super.onDestroy()
        Log.d("M_MainActivity", "onDestroy")
    }

    /**
     * Этот метод сохраняет состояние представления в Bundle
     * Для Api Level < 28 (Android P) этот метод будет выполняться до onStop(), и нет никаких гарантий относительного того,
     * произойдет ли это до или после onPause().
     * Для API Level >=28 будет вызван после onStop()
     * Не будет вызыван если Activity будет явно закрыто пользователем при нажатии на системную клавишу back
     */
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putBoolean(IS_EDIT_MODE, isEditMode)

//        outState?.putString("STATUS",benderObj.status.name)
//        outState?.putString("QUESTION",benderObj.question.name)
//        Log.d("M_MainActivity","onSaveInstanceState ${benderObj.status.name} ${benderObj.question.name}")
    }

//    override fun onClick(v: View?) {
//        if (v?.id == R.id.iv_send) {
//            if (this.isKeyboardOpen()){
//                Log.d("M_MainActivity","Клавиатура открыта")
//            }   else    {
//                Log.d("M_MainActivity","Клавиатура закрыта")
//            }
//            if (!this.isKeyboardClosed()){
//                Log.d("M_MainActivity","Клавиатура открыта")
//            }   else    {
//                Log.d("M_MainActivity","Клавиатура закрыта")
//            }
//
//            val (phase, color) = benderObj.listenAnswer( messageEt.text.toString())
//            messageEt.setText("")
//            val (r,g,b) = color
//            benderImage.setColorFilter(Color.rgb(r,g,b),PorterDuff.Mode.MULTIPLY)
//            textTxt.text = phase
//            hideKeyboard()
//        }
//    }

}
