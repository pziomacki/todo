package com.ziomacki.todo.taskslist.model

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
            sut.fetchNextPartOfTasks(0).subscribe(testSubscriber)
        then:
            testSubscriber.assertNoErrors()
            1 * taskListRepositoryMock.updateTaskContainer(taskContainer)
    }

    def "don't store TaskContainer on network request connected error"() {
        given:
            TaskListRepository taskListRepositoryMock = Mock(TaskListRepository)
            TodoService todoServiceStub = Stub(TodoService)
            todoServiceStub.fetchTasks(_ as Integer) >> Observable.error(new IllegalArgumentException(""))
            TestSubscriber testSubscriber = new TestSubscriber()
            FetchList sut = new FetchList(taskListRepositoryMock, todoServiceStub)
        when:
            sut.fetchNextPartOfTasks(0).subscribe(testSubscriber)
        then:
            testSubscriber.assertError(IllegalArgumentException)
            0 * taskListRepositoryMock.updateTaskContainer(_ as TaskContainer)
    }
}
