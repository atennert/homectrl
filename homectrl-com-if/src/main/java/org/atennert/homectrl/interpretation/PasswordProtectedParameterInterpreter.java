/*******************************************************************************
 * Copyright 2016 Andreas Tennert
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
package org.atennert.homectrl.interpretation;

import org.atennert.com.communication.IDataAcceptance;
import org.atennert.com.util.DataContainer;
import org.atennert.com.util.MapDataContainer;
import org.atennert.com.util.MessageContainer;
import org.atennert.com.util.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Scheduler;
import rx.Single;
import rx.SingleSubscriber;

import java.util.Map;

/**
 * This interpreter checks accounts fields in the data. If the account data is correct, then the data will
 * be forwarded to the server. Otherwise it won't. This Interpreter uses the {@link ParameterInterpreter}
 * to encode and decode the data. It removes the name and pw-fields from the data before forwarding it to
 * the server. The fields must literally be named "name" and "pw". If the data includes a field with the
 * key "id", then the content of this field will replace the sender address for the application.
 */
public class PasswordProtectedParameterInterpreter extends ParameterInterpreter {

    private static Logger log = LoggerFactory.getLogger( ParameterInterpreter.class );

    /** Holds names and passwords */
    private final Map<String, String> accountData;

    public PasswordProtectedParameterInterpreter(Map<String, String> accountData) {
        // TODO encrypt data
        this.accountData = accountData;
    }

    @Override
    public void interpret(MessageContainer messageContainer, Session session, IDataAcceptance acceptance, Scheduler scheduler) {
        log.trace( "interpreting: " + messageContainer.message );

        if (messageContainer.hasException() && !MessageContainer.Exception.EMPTY.equals( messageContainer.error )){
            session.call(null);
            return;
        }

        Map<String, Object> map = translateMessage( messageContainer.message );

        final String name = (String) map.remove("name");
        final String pw = (String) map.remove("pw");

        if (!checkNameAndPw(name, pw))
        {
            session.call(null);
            return;
        }

        final String sender = map.containsKey("id")
                ? (String) map.remove("id")
                : session.getSender();

        acceptance.accept( sender, new MapDataContainer(map,
                new SingleSubscriber<DataContainer>() {
                    @Override
                    public void onSuccess(DataContainer dataContainer) {
                        Single.just(dataContainer)
                                .subscribeOn(session.scheduler)
                                .map(data -> encode(data))
                                .subscribe(session);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        log.error("[ParameterInterpreter.Subscriber.accept] ERROR");
                        Single.<String>just(null)
                                .subscribeOn(session.scheduler)
                                .subscribe(session);
                    }
                }) );
    }

    /**
     * @param name The account name
     * @param pw The account password
     * @return <code>true</code> if name and password are known, <code>false</code> otherwise
     */
    private boolean checkNameAndPw(final String name, final String pw) {
        return !(name == null || pw == null)
                && accountData.get(name) != null
                && accountData.get(name).equals(pw);
    }
}
