package com.clloret.days.events.create;

import static com.clloret.days.events.SampleBuilder.createEventViewModel;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.clloret.days.domain.AppDataStore;
import com.clloret.days.domain.entities.Event;
import com.clloret.days.events.SampleBuilder;
import com.clloret.days.model.entities.EventViewModel;
import com.clloret.days.model.entities.mapper.EventViewModelMapper;
import com.clloret.days.model.entities.mapper.TagViewModelMapper;
import com.clloret.days.utils.RxImmediateSchedulerRule;
import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class EventCreatePresenterTest {

  @ClassRule
  public static final RxImmediateSchedulerRule schedulers = new RxImmediateSchedulerRule();

  @Mock
  private AppDataStore appDataStore;

  @Mock
  private EventViewModelMapper eventViewModelMapper;

  @Mock
  private TagViewModelMapper tagViewModelMapper;

  @Mock
  private EventBus eventBus;

  @Mock
  private EventCreateView eventCreateView;

  @InjectMocks
  private EventCreatePresenter eventCreatePresenter;

  @Before
  public void setUp() {

    MockitoAnnotations.initMocks(this);

    eventCreatePresenter.attachView(eventCreateView);
  }

  @Test
  public void createEvent_Always_CallApiAndNotifyView() {

    final Event event = SampleBuilder.createEvent();
    final EventViewModel eventViewModel = SampleBuilder.createEventViewModel();

    when(appDataStore.createEvent(any())).thenReturn(new Maybe<Event>() {
      @Override
      protected void subscribeActual(MaybeObserver<? super Event> observer) {

        observer.onSuccess(event);
      }
    });

    addStubMethodsToMapper(event, eventViewModel);

    eventCreatePresenter.createEvent(eventViewModel);

    verify(appDataStore).createEvent(any());
    verify(eventCreateView).onSuccessfully(eq(eventViewModel));
  }

  private void addStubMethodsToMapper(Event event, EventViewModel eventViewModel) {

    when(eventViewModelMapper.fromEvent(Mockito.any(Event.class))).thenReturn(eventViewModel);
    when(eventViewModelMapper.toEvent(Mockito.any(EventViewModel.class))).thenReturn(event);
  }

  @Test
  public void createEvent_WhenEmptyName_NotifyViewError() {

    final EventViewModel eventViewModel = createEventViewModel();
    eventViewModel.setName(SampleBuilder.emptyText);

    eventCreatePresenter.createEvent(eventViewModel);

    verify(eventCreateView).onEmptyEventNameError();
    verifyNoMoreInteractions(appDataStore);
  }
}