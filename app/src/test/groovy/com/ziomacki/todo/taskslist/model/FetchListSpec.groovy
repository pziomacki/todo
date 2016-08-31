package com.ziomacki.todo.taskslist.model

import com.ziomacki.todo.taskdetails.model.Task
import rx.Observable
import rx.observers.TestSubscriber
import spock.lang.Specification

class FetchListSpec extends Specification {

    def "store fetched TaskContainer"() {
        given:
            TaskContainer taskContainer = new TaskContainer()
            TaskListRepository taskListRepositoryMock = Mock(TaskListRepository)
            TodoService todoServiceStub = Stub(TodoService)
            todoServiceStub.fetchTasks(_ as Integer) >> Observable.just(taskContainer)
            TestSubscriber testSubscriber = new TestSubscriber()
            FetchList sut = new FetchList(taskListRepositoryMock, todoServiceStub)
        when:
            sut.fetchNextTasksAndReturnTotalCount(0).subscribe(testSubscriber)
        then:
            1 * taskListRepositoryMock.updateTaskList(taskContainer.taskList)
    }

    def "return totalCount of tasks"() {
        given:
            TaskContainer taskContainer = new TaskContainer()
            taskContainer.totalCount = 1
            TaskListRepository taskListRepositoryMock = Mock(TaskListRepository)
            TodoService todoServiceStub = Stub(TodoService)
            todoServiceStub.fetchTasks(_ as Integer) >> Observable.just(taskContainer)
            TestSubscriber testSubscriber = new TestSubscriber()
            FetchList sut = new FetchList(taskListRepositoryMock, todoServiceStub)
        when:
            sut.fetchNextTasksAndReturnTotalCount(0).subscribe(testSubscriber)
            int count = testSubscriber.onNextEvents.get(0)
        then:
            count == 1
    }

    def "don't store TaskContainer on network request connected error"() {
        given:
            TaskListRepository taskListRepositoryMock = Mock(TaskListRepository)
            TodoService todoServiceStub = Stub(TodoService)
            todoServiceStub.fetchTasks(_ as Integer) >> Observable.error(new IllegalArgumentException(""))
            TestSubscriber testSubscriber = new TestSubscriber()
            FetchList sut = new FetchList(taskListRepositoryMock, todoServiceStub)
        when:
            sut.fetchNextTasksAndReturnTotalCount(0).subscribe(testSubscriber)
        then:
            testSubscriber.assertError(IllegalArgumentException)
            0 * taskListRepositoryMock.updateTaskList(_ as List<Task>)
    }
}
