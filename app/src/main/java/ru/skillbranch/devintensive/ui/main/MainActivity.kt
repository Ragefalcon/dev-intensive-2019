package ru.skillbranch.devintensive.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.ListPaddingDecoration
import ru.skillbranch.devintensive.models.data.ChatType
import ru.skillbranch.devintensive.repositories.PreferencesRepository
import ru.skillbranch.devintensive.ui.adapters.ChatAdapter
import ru.skillbranch.devintensive.ui.adapters.ChatItemTouchHelperCallback
import ru.skillbranch.devintensive.ui.archive.ArchiveActivity
import ru.skillbranch.devintensive.ui.group.GroupActivity
import ru.skillbranch.devintensive.viewmodels.MainViewModel
import android.R.id
import android.widget.TextView
import ru.skillbranch.devintensive.extensions.config


class MainActivity : AppCompatActivity() {

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initToolbar()
        initViews()
        initViewModel()
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
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

    private fun initViews() {
        chatAdapter = ChatAdapter {
            if (it.chatType != ChatType.ARCHIVE) {
                Snackbar.make(rv_chat_list, "Click on ${it.title}", Snackbar.LENGTH_LONG)
                    .config()
                    .show()
            } else {
                Log.d("M_MainActivity", "FabButtonClick0")
                val intent = Intent(this, ArchiveActivity::class.java)
                Log.d("M_MainActivity", "FabButtonClick1")
                startActivity(intent)
                Log.d("M_MainActivity", "FabButtonClick2")
            }
        }
        val divider = ListPaddingDecoration(
            this,
            (resources.getDimension(R.dimen.spacing_normal_16) * 2 + resources.getDimension(R.dimen.avatar_item_size)).toInt(),
            0
        ) //DividerItemDecoration(this,DividerItemDecoration.VERTICAL)
        val touchCallback = ChatItemTouchHelperCallback(chatAdapter) {
            //            Log.d("M_MainActivity","${it.chatType}")
//            if(it.chatType!=ChatType.ARCHIVE) {
            viewModel.addToArcheve(it.id)
            class MyUndoListener : View.OnClickListener {

                override fun onClick(v: View) {
                    viewModel.restoreFromArchive(it.id) // Code to undo the user's last action
                }
            }

            Snackbar.make(rv_chat_list, "Вы точно хотите добавить ${it.title} в архив?", Snackbar.LENGTH_LONG)
                .setAction("Отмена", MyUndoListener())
                .config()
                .show()
        }// если лябмда выражение является последним аргументом ее можно вынести за скобки

        val touchHelper = ItemTouchHelper(touchCallback)
        touchHelper.attachToRecyclerView(rv_chat_list)

        with(rv_chat_list) {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            addItemDecoration(divider)
        }


        fab.setOnClickListener {
            Log.d("M_MainActivity", "FabButonClick")
            val intent = Intent(this, GroupActivity::class.java)
            startActivity(intent)

            //       viewModel.addItems()
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.getChatData().observe(this, Observer { chatAdapter.updateData(it) })
    }
}
