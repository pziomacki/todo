package com.ziomacki.todo.component

import android.content.Context
import spock.lang.Specification

class ResourceProviderSpec extends Specification {
    def "load String from resources"() {
        given:
            Context contextMock = Mock(Context)
            ResourceProvider sut = new ResourceProvider(contextMock)
        when:
            sut.getString(0)
        then:
            1 * contextMock.getString(0)
    }

}
