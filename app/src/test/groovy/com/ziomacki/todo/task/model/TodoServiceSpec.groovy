package com.ziomacki.todo.task.model

import okhttp3.Headers
import retrofit2.Response
import rx.observers.TestSubscriber
import spock.lang.Specification

class TodoServiceSpec extends Specification {

    def "parse response and return TaskContainer"() {
        given:
            TodoApiService todoApiServiceStub = Stub(TodoApiService)
            Headers headersStub = Stub(Headers)
            headersStub.get("X-Total-Count") >> "1"
            Response<List<Task>> responseStub = Stub(Response)
            responseStub.headers() >> headersStub
            responseStub.body() >> getTestTasks()
            TodoService sut = new TodoService(todoApiServiceStub)
            TestSubscriber<TaskContainer> testSubscriber = new TestSubscriber<>()
        when:
            sut.fetchTasks(0).subscribe(testSubscriber)
            TaskContainer result = testSubscriber.onNextEvents.get(0)
        then:
            result.totalCount == 1
            result.taskRealmList.size() == 1
            result.taskRealmList.get(0).title.equals("task")
    }

    def List<Task> getTestTasks() {
        List<Task> list = new ArrayList<>()
        Task task1 = new Task()
        task1.id = 1;
        task1.title = "task"
        task1.userId = 1;
        testTasks.add(task1)
    }
}
