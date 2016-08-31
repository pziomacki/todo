package com.ziomacki.todo.taskslist.model

import com.ziomacki.todo.taskdetails.model.Task
import okhttp3.Headers
import retrofit2.Response
import rx.Observable
import rx.observers.TestSubscriber
import spock.lang.Specification

class TodoServiceSpec extends Specification {

    def "parse response and return TaskContainer"() {
        given:
            TestSubscriber<TaskContainer> testSubscriber = new TestSubscriber<>()
            Headers headers = new Headers.Builder().add("X-Total-Count", "1").build()
            Response<List<Task>> response = Response.success(getTestTasks(), headers)
            TodoApiService todoApiServiceStub = Stub(TodoApiService)
            todoApiServiceStub.getTaskList(_ as Integer, _ as Integer) >> Observable.just(response)
            TodoService sut = new TodoService(todoApiServiceStub)
        when:
            sut.fetchTasks(0).subscribe(testSubscriber)
            TaskContainer result = testSubscriber.getOnNextEvents().get(0)
        then:
            result.totalCount == 1
            result.taskList.size() == 1
            result.taskList.get(0).title.equals("task")
    }

    def "parse response and return TaskContainer when server returns empty list"() {
        given:
            TestSubscriber<TaskContainer> testSubscriber = new TestSubscriber<>()
            Headers headers = new Headers.Builder().add("X-Total-Count", "0").build()
            Response<List<Task>> response = Response.success(new ArrayList<Task>(), headers)
            TodoApiService todoApiServiceStub = Stub(TodoApiService)
            todoApiServiceStub.getTaskList(_ as Integer, _ as Integer) >> Observable.just(response)
            TodoService sut = new TodoService(todoApiServiceStub)
        when:
            sut.fetchTasks(0).subscribe(testSubscriber)
            TaskContainer result = testSubscriber.getOnNextEvents().get(0)
        then:
            result.totalCount == 0
            result.taskList.size() == 0
    }

    def List<Task> getTestTasks() {
        List<Task> list = new ArrayList<>()
        Task task1 = new Task()
        task1.id = 1
        task1.title = "task"
        task1.userId = 1
        list.add(task1)
        return list
    }
}
