package com.clloret.days.tags.create;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.clloret.days.domain.AppDataStore;
import com.clloret.days.domain.entities.Tag;
import com.clloret.days.model.entities.TagViewModel;
import com.clloret.days.model.entities.mapper.TagViewModelMapper;
import com.clloret.days.tags.SampleBuilder;
import com.clloret.days.utils.RxImmediateSchedulerRule;
import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class TagCreatePresenterTest {

  @ClassRule
  public static final RxImmediateSchedulerRule schedulers = new RxImmediateSchedulerRule();

  @Mock
  private AppDataStore appDataStore;

  @Mock
  private TagViewModelMapper tagViewModelMapper;

  @Mock
  private TagCreateView tagCreateView;

  @InjectMocks
  private TagCreatePresenter tagCreatePresenter;

  @Before
  public void setUp() {

    MockitoAnnotations.initMocks(this);

    tagCreatePresenter.attachView(tagCreateView);
  }

  @Test
  public void createTag_Always_CallApiAndNotifyView() {

    Tag tag = SampleBuilder.createTag();
    TagViewModel tagViewModel = SampleBuilder.createTagViewModel();

    when(appDataStore.createTag(any())).thenReturn(new Maybe<Tag>() {
      @Override
      protected void subscribeActual(MaybeObserver<? super Tag> observer) {

        observer.onSuccess(tag);
      }
    });

    addStubMethodsToMapper(tag, tagViewModel);

    tagCreatePresenter.createTag(SampleBuilder.name);

    verify(appDataStore).createTag(any());
    verify(tagCreateView).onSuccessfully(eq(tagViewModel));
  }

  private void addStubMethodsToMapper(Tag tag, TagViewModel tagViewModel) {

    when(tagViewModelMapper.fromTag(Mockito.any(Tag.class))).thenReturn(tagViewModel);
    when(tagViewModelMapper.toTag(Mockito.any(TagViewModel.class))).thenReturn(tag);
  }

  @Test
  public void createTag_WhenEmptyName_NotifyViewError() {

    tagCreatePresenter.createTag(SampleBuilder.emptyText);

    verify(tagCreateView).onEmptyTagNameError();
    verifyNoMoreInteractions(appDataStore);
  }

}