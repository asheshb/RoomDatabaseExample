package com.example.roomdatabaseexample

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskDetailViewModel(application: Application): AndroidViewModel(application){
    private val repo: TaskDetailRepository =
        TaskDetailRepository(application)

    private val _taskId = MutableLiveData<Long>(0)

    val taskId: LiveData<Long>
        get() = _taskId

    val task: LiveData<Task> = Transformations
        .switchMap(_taskId) { id ->
            repo.getTask(id)
        }

    fun setTaskId(id: Long){
        if(_taskId.value != id ) {
            _taskId.value = id
        }
    }

    fun saveTask(task: Task){
        viewModelScope.launch(Dispatchers.IO) {
            if (_taskId.value == 0L) {
                _taskId.postValue(repo.insertTask(task))
            } else {
                repo.updateTask(task)
            }
        }
    }

    fun deleteTask(){
        viewModelScope.launch(Dispatchers.IO) {
            task.value?.let { repo.deleteTask(it) }
        }
    }
}