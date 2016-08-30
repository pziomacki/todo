package com.ziomacki.todo.taskslist.model

import com.ziomacki.todo.taskdetails.model.Task
import io.realm.RealmList
import spock.lang.Specification

class TaskContainerTest extends Specification {

    def "Return false for checking when only part is fetched"() {
        given:
            RealmList<Task> list = new RealmList<>()
            list.add(new Task())
            TaskContainer sut = new TaskContainer()
            sut.taskRealmList = list
            sut.totalCount = 20
        when:
            boolean allFetched = sut.isAllFetched()
        then:
            allFetched == false
    }

    def "Return true for checking when all tasks are fetched"() {
        given:
            RealmList<Task> list = new RealmList<>()
            list.add(new Task())
            TaskContainer sut = new TaskContainer()
            sut.taskRealmList = list
            sut.totalCount = 1
        when:
            boolean allFetched = sut.isAllFetched()
        then:
            allFetched == true
    }
}
