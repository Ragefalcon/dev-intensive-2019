package ru.skillbranch.devintensive.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import ru.skillbranch.devintensive.extensions.mutableLiveData
import ru.skillbranch.devintensive.models.data.UserItem
import ru.skillbranch.devintensive.repositories.GroupRepository

class GroupViewModel : ViewModel() {
    private val query = mutableLiveData("")
    private val groupRepository = GroupRepository
    private val userItems = mutableLiveData(loadUsers())
    private val selectedItems = Transformations.map(userItems){ users -> users.filter { it.isSelected } }

    fun getUsersData(): LiveData<List<UserItem>> {
        Log.d("M_GroupViewModel","getUserData1")
        val result = MediatorLiveData<List<UserItem>>()

        val filterF = {
            val queryStr = query.value!!
            val users = userItems.value!!

            result.value = if(queryStr.isEmpty())   users
            else users.filter { it.fullName.contains(queryStr, true) }
        }

        result.addSource(userItems){filterF.invoke()}
        result.addSource(query){filterF.invoke()}
        Log.d("M_GroupViewModel","getUserData2")

        return  result
    }

    fun getSelectedData(): LiveData<List<UserItem>> = selectedItems

    fun handleSelectedItem(userId: String) {
        Log.d("M_GroupViewModel","selectedItem")
        userItems.value = userItems.value!!.map{
            if (it.id == userId) it.copy(isSelected = !it.isSelected)
            else it
        }
    }

    fun handleRemoveChip(userId: String) {
        Log.d("M_GroupViewModel","removeItem")
        userItems.value = userItems.value!!.map{
            if (it.id == userId) it.copy(isSelected = false)
            else it
        }
    }

    fun handleSearchQuery(text: String?) {
        Log.d("M_GroupViewModel","handleSearchQuery")
        query.value = text
    }

    private fun loadUsers(): List<UserItem> = GroupRepository.loadUsers().map{ it.toUserItem() }
    fun handleCreateGroup() {
        groupRepository.createChat(selectedItems.value!!)
    }
}