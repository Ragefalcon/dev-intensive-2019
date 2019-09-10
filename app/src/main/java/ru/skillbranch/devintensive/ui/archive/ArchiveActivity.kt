package ru.skillbranch.devintensive.ui.archive

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_archive.*
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.ListPaddingDecoration
import ru.skillbranch.devintensive.extensions.config
import ru.skillbranch.devintensive.ui.adapters.ArchiveChatItemTouchHelperCallback
import ru.skillbranch.devintensive.ui.adapters.ChatAdapter
import ru.skillbranch.devintensive.ui.adapters.ChatItemTouchHelperCallback
import ru.skillbranch.devintensive.viewmodels.ArchiveViewModel

class ArchiveActivity : AppCompatActivity() {

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var viewModel: ArchiveViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("M_ArchiveActivity","00")
        super.onCreate(savedInstanceState)
        Log.d("M_ArchiveActivity","0")
        setContentView(R.layout.activity_archive)
        Log.d("M_ArchiveActivity","1")
        initToolbar()
        Log.d("M_ArchiveActivity","2")
        initViews()
        Log.d("M_ArchiveActivity","3")
        initViewModel()
        Log.d("M_ArchiveActivity","4")
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        Log.d("M_ArchiveActivity","1234")
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

    private fun initViews() {
        chatAdapter = ChatAdapter{
            Snackbar.make(rv_archive_list, "Click on ${it.title}",Snackbar.LENGTH_LONG)
                .config()
                .show()
        }
        val divider = ListPaddingDecoration(this,(resources.getDimension(R.dimen.spacing_normal_16)*2+resources.getDimension(R.dimen.avatar_item_size)).toInt(),0) //DividerItemDecoration(this,DividerItemDecoration.VERTICAL)
        val touchCallback = ArchiveChatItemTouchHelperCallback(chatAdapter){
            viewModel.restoreFromArchive(it.id)
            class MyUndoListener : View.OnClickListener {

                override fun onClick(v: View) {
                    viewModel.addToArchive(it.id) // Code to undo the user's last action
                }
            }
            Snackbar.make(rv_archive_list, "Восстановить чат с ${it.title} из архива?", Snackbar.LENGTH_LONG)
                .config()
                .setAction("Отмена", MyUndoListener())
                .show()
        }// если лябмда выражение является последним аргументом ее можно вынести за скобки

        val touchHelper = ItemTouchHelper(touchCallback)
        touchHelper.attachToRecyclerView(rv_archive_list)

        with(rv_archive_list){
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(this@ArchiveActivity)
            addItemDecoration(divider)
        }

    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(ArchiveViewModel::class.java)
        viewModel.getChatData().observe(this, Observer { chatAdapter.updateData(it) })
    }
}
