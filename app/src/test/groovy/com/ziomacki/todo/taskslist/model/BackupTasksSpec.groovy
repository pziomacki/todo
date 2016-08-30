package com.ziomacki.todo.taskslist.model

import com.ziomacki.todo.taskdetails.model.Task
import rx.Observable
import rx.observers.TestSubscriber
import spock.lang.Specification

class BackupTasksSpec extends Specification {

    def "return list of nonmodified tasks after backup"() {
        given:
            Task task = new Task()
            task.modified = true
            List<Task> tasks = new ArrayList<>()
            tasks.add(task)
            TodoService todoServiceStub = Stub(TodoService)
            todoServiceStub.backupTasks(tasks) >> Observable.just(tasks)
            TaskListRepository taskListRepositoryStub = Stub(TaskListRepository)
            taskListRepositoryStub.getUnmanagedModifiedTasks() >> Observable.just(tasks)
            BackupTasks sut = new BackupTasks(todoServiceStub, taskListRepositoryStub)
            TestSubscriber testSubscriber = new TestSubscriber()
        when:
            sut.backup().subscribe(testSubscriber)
            List<Task> tasksResult = testSubscriber.getOnNextEvents().get(0);
        then:
            tasksResult.get(0).modified == false
    }

    def "do not update local cache if server respond with error"() {
        given:
            List<Task> tasks = new ArrayList<>()
            TodoService todoServiceStub = Stub(TodoService)
            todoServiceStub.backupTasks(tasks) >> Observable.error(new IllegalArgumentException(""))
            TaskListRepository taskListRepositoryStub = Mock(TaskListRepository)
            taskListRepositoryStub.getUnmanagedModifiedTasks() >> Observable.just(tasks)
            BackupTasks sut = new BackupTasks(todoServiceStub, taskListRepositoryStub)
            TestSubscriber testSubscriber = new TestSubscriber()
        when:
            sut.backup().subscribe(testSubscriber)
        then:
            testSubscriber.assertError(IllegalArgumentException)
            0 * taskListRepositoryStub.updateTaskList(_ as List<Task>)

    }
}
