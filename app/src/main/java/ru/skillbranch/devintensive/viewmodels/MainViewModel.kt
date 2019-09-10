package ru.skillbranch.devintensive.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import ru.skillbranch.devintensive.extensions.mutableLiveData
import ru.skillbranch.devintensive.extensions.shortFormat
import ru.skillbranch.devintensive.models.BaseMessage
import ru.skillbranch.devintensive.models.data.ChatItem
import ru.skillbranch.devintensive.models.data.ChatType
import ru.skillbranch.devintensive.models.data.UserItem
import ru.skillbranch.devintensive.repositories.ChatRepository
import ru.skillbranch.devintensive.utils.DataGenerator
import java.util.*

class MainViewModel : ViewModel() {
    private val query = mutableLiveData("")
    private val chatRepository = ChatRepository
    private val chats = Transformations.map(chatRepository.loadChats()) { chats ->
        return@map chats.filter { !it.isArchived }
            .map { it.toChatItem() }
            .sortedBy { it.id.toInt() }
    } //mutableLiveData(loadChats())

    fun getChatData(): LiveData<List<ChatItem>> {
        val result = MediatorLiveData<List<ChatItem>>()

        val filterF = {
            val queryStr = query.value!!
            val users = mutableListOf<ChatItem>()
            val archiveChat = chatRepository.loadChats().value?.filter { it.isArchived }?.toMutableList()
            if (archiveChat?.count() ?: 0 != 0 && queryStr.isEmpty()) {//chats.value!!.count()) {
                var messCount = 0
                var messDat: Date = Date()
                var messAr:MutableList<BaseMessage>
                var lastMes : Pair<String, String?> = "" to ""
                archiveChat?.forEach {
//                    messAr = it.messages?.filter { !it.isReaded }.toMutableList()
//                        messAr.sortByDescending { it.date }
                    if (it.messages.count()!=0){
                        if(messCount == 0) {
                            lastMes = it.lastMessageShort()
                            messDat = it.lastMessageDate()!!
                        } else if (messDat<it.lastMessageDate()) {
                            lastMes = it.lastMessageShort()
                            messDat = it.lastMessageDate()!!
                        }
                        messCount += it.unreadableMessageCount()
                    }
//                    if (it.unreadableMessageCount()!=0){
//                        if(messCount == 0) {
//                            lastMes = it.lastMessageShort()
//                            messDat = it.lastMessageDate()!!
//                        } else if (messDat<it.lastMessageDate()) {
//                            lastMes = it.lastMessageShort()
//                            messDat = it.lastMessageDate()!!
//                        }
//                        messCount += it.unreadableMessageCount()
//                    }
                }
                users.add(
                    ChatItem(
                        "${users.size}",
                        null,
                        "",
                        "",
                        lastMes.first,
                        messCount,//unreadableMessageCount(),
                        messDat?.shortFormat(),
                        false,
                        ChatType.ARCHIVE,
                        if(lastMes.second!="") "@${lastMes.second}" else lastMes.second
                    )
                )
            } //else  {
            users.addAll(chats.value!!.toMutableList())
            //   }
            result.value = if (queryStr.isEmpty()) users
            else users.filter { it.title.contains(queryStr, true) }
        }

        result.addSource(chats) { filterF.invoke() }
        result.addSource(query) { filterF.invoke() }
        return result
    }

//    private fun loadChats(): List<ChatItem> {
//        val chats = chatRepository.loadChats()
//        return chats.map {it.toChatItem()}
//            .sortedBy { it.id.toInt() }
//    }
//
//    fun addItems() {
//        val newItems = DataGenerator.generateChatsWithOffset(chats.value!!.size,5).map { it.toChatItem() }
//        val copy = chats.value!!.toMutableList()
//        copy.add(newItems)
//        chats.value = copy.sortedBy { it.id.toInt() }
//        Log.d("M_MainViewModel","FunAddItem")
//    }

    fun addToArcheve(chatId: String) {
        val chat = chatRepository.find(chatId)
        chat ?: return
        chatRepository.update(chat.copy(isArchived = true))
    }

    fun restoreFromArchive(chatId: String) {
        val chat = chatRepository.find(chatId)
        chat ?: return
        chatRepository.update(chat.copy(isArchived = false))
    }

    fun handleSearchQuery(text: String?) {
        Log.d("M_GroupViewModel", "handleSearchQuery")
        query.value = text
    }
}