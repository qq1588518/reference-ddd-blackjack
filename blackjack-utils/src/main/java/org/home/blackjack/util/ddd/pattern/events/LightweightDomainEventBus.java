//   Copyright 2012,2013 Vaughn Vernon
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

package org.home.blackjack.util.ddd.pattern.events;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Scope;

import javax.annotation.Resource;
import javax.inject.Named;

@Named
@Scope("prototype")
public class LightweightDomainEventBus implements DomainEventPublisher, SubscribableEventBus {

	private final static Executor EXECUTOR = Executors.newFixedThreadPool(100);
	private static Logger LOGGER = Logger.getLogger(LightweightDomainEventBus.class);

    private boolean publishing;

    @Resource
    private List<DomainEventSubscriber> subscribers;

	private List<DomainEvent> bufferedEvents = Lists.newArrayList();

	public <T extends DomainEvent> void publish(final T aDomainEvent) {
		if (!this.isPublishing() && this.hasSubscribers()) {

			try {
				this.setPublishing(true);
				bufferedEvents.add(aDomainEvent);
			} finally {
				this.setPublishing(false);
			}
		}
	}

	public void reset() {
		if (!this.isPublishing()) {
            bufferedEvents.clear();
		}
	}

	public <T extends DomainEvent> void register(DomainEventSubscriber<T> aSubscriber) {
		if (!this.isPublishing()) {
			this.subscribers().add(aSubscriber);
		}
	}

	private LightweightDomainEventBus() {
		super();

		this.setPublishing(false);
	}

	private boolean isPublishing() {
		return this.publishing;
	}

	private void setPublishing(boolean aFlag) {
		this.publishing = aFlag;
	}

	private boolean hasSubscribers() {
		return this.subscribers() != null;
	}

	private List<DomainEventSubscriber> subscribers() {
		return this.subscribers;
	}

	public void flush() {

		List<DomainEventSubscriber> allSubscribers = this.subscribers();

		while (!bufferedEvents.isEmpty()) {
			final DomainEvent nextEvent = bufferedEvents.remove(0);

			for (final DomainEventSubscriber subscriber : allSubscribers) {

				if (subscriber.subscribedTo(nextEvent)) {
					EXECUTOR.execute(new Runnable() {
						@Override
						public void run() {
							subscriber.handleEvent(nextEvent);
						}
					});
				}
			}
		}
	}
}
