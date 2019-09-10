package ru.skillbranch.devintensive.ui.group

import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.activity_group.*
import kotlinx.android.synthetic.main.activity_profile.view.*
import kotlinx.android.synthetic.main.item_chat_single.*
import kotlinx.android.synthetic.main.item_user_list.*
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.ListPaddingDecoration
import ru.skillbranch.devintensive.models.data.UserItem
import ru.skillbranch.devintensive.repositories.PreferencesRepository
import ru.skillbranch.devintensive.ui.adapters.UserAdapter
import ru.skillbranch.devintensive.ui.custom.AvatarImageView
import ru.skillbranch.devintensive.viewmodels.GroupViewModel

class GroupActivity : AppCompatActivity() {

    private lateinit var usersAdapter: UserAdapter
    private lateinit var viewModel: GroupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)
        initToolbar()
        initViews()
        initViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search,menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = "Введите имя пользователя"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.handleSearchQuery(query)
                return true
            }

            override fun onQueryTextChange(nextText: String?): Boolean {
                viewModel.handleSearchQuery(nextText)
                return true
            }

        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if(item?.itemId == android.R.id.home){
            finish()
            overridePendingTransition(R.anim.idle,R.anim.bottom_down)
            true
        }   else    {
            super.onOptionsItemSelected(item)
        }
    }
    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initViews() {
        usersAdapter = UserAdapter { viewModel.handleSelectedItem(it.id) }
//        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        val divider = ListPaddingDecoration(this,(resources.getDimension(R.dimen.spacing_normal_16)*2+resources.getDimension(R.dimen.avatar_item_size)).toInt(),0)
        with(rv_user_list) {
            adapter = usersAdapter
            layoutManager = LinearLayoutManager(this@GroupActivity)
            addItemDecoration(divider)
        }

        fab.setOnClickListener{
            viewModel.handleCreateGroup()
            finish()
            overridePendingTransition(R.anim.idle,R.anim.bottom_down)
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)
        viewModel.getUsersData().observe(this, Observer { usersAdapter.updateData(it) })
        viewModel.getSelectedData().observe(this, Observer {
            updateChips(it)
            toggleFab(it.size>1)
        })
    }

    private fun toggleFab(isShow: Boolean) {
        if(isShow) fab.show()
        else fab.hide()
    }

    private fun addChipToGroup(user: UserItem) {
        val color = if (PreferencesRepository.getAppTheme() == AppCompatDelegate.MODE_NIGHT_NO) {
            resources.getColor(R.color.color_primary_light, theme)
        } else {
            resources.getColor(R.color.color_gray_dark2, theme)
        }
        val color2 = if (PreferencesRepository.getAppTheme() == AppCompatDelegate.MODE_NIGHT_NO) {
            resources.getColor(R.color.color_white, theme)
        } else {
            resources.getColor(R.color.color_gray, theme)
        }
//        usersAdapter. items.find {  }
        var img = AvatarImageView(this)

        val chip = Chip(this).apply {
            text = user.fullName
            chipIcon = resources.getDrawable(R.drawable.avatar_default, theme)
            isCloseIconVisible = true
            tag = user.id
            isClickable = true
            closeIconTint = ColorStateList.valueOf(color2)
            chipBackgroundColor = ColorStateList.valueOf(color)//getColor(R.color.color_primary_light))
            setTextColor(Color.WHITE)
        }
//        if (user.avatar == null) {
////            Glide.with(chip) // передаем источник контекста
////                .clear(iv_avatar_single)
////            // а здесь нужно включать experimental в build.gradle app
////            iv_avatar_single.setInitials(user.initials ?: "")
//            img.setInitials(user.initials ?: "??")// setImageDrawable(resources.getDrawable(R.drawable.avatar_default, theme))
//        }   else    {
//            chip.chipIcon = Glide.with(chip) // передаем источник контекста
//                .load(user.avatar) // передаем uri/url
//                .fallbackDrawable// into(chip.chipIcon) // таргет в который должны вставить изображение
//
//        }
        chip.setOnCloseIconClickListener { viewModel.handleRemoveChip(it.tag.toString()) }
        chip_group.addView(chip)
    }

    private fun updateChips(listUsers: List<UserItem>) {

        chip_group.visibility = if (listUsers.isEmpty()) View.GONE else View.VISIBLE

        // превращает коллекцию в мапу, применив к ней какую-то трансформ функцию
        val users = listUsers.associate { user -> user.id to user }
            .toMutableMap()
        val views = chip_group.children.associate { view -> view.tag to view }

        for((k, v) in views) {
            if (!users.containsKey(k)) chip_group.removeView(v)
            else users.remove(k)
        }
        users.forEach{(_, v) -> addChipToGroup(v)}
    }
}
