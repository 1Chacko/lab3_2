package edu.iis.mto.staticmock.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import edu.iis.mto.staticmock.Configuration;
import edu.iis.mto.staticmock.ConfigurationLoader;
import edu.iis.mto.staticmock.IncomingInfo;
import edu.iis.mto.staticmock.IncomingNews;
import edu.iis.mto.staticmock.NewsLoader;
import edu.iis.mto.staticmock.NewsReaderFactory;
import edu.iis.mto.staticmock.PublishableNews;
import edu.iis.mto.staticmock.SubsciptionType;
import edu.iis.mto.staticmock.reader.NewsReader;

import static org.powermock.api.mockito.PowerMockito.*;

import java.util.ArrayList;
import java.util.List;

import org.mockito.internal.util.reflection.*;
import static org.hamcrest.CoreMatchers.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ConfigurationLoader.class, NewsReaderFactory.class, PublishableNews.class})

public class NewsLoaderTest {
	
	private ConfigurationLoader testConfigurationLoader = null;
	private Configuration testConfiguration = null;
	private List<String> exampleList = null;
	private String readerExample = "AB";
	
	@Before
	public void setUp() throws Exception {
		testConfigurationLoader = PowerMockito.mock(ConfigurationLoader.class);
		PowerMockito.mockStatic(ConfigurationLoader.class);
		PowerMockito.when(ConfigurationLoader.getInstance()).thenReturn(testConfigurationLoader);
		
		testConfiguration = new Configuration();
		Whitebox.setInternalState(testConfiguration, "readerType", "testNewsReader");
		when(testConfigurationLoader.loadConfiguration()).thenReturn(testConfiguration);
		
		PowerMockito.mockStatic(NewsReaderFactory.class);
		final IncomingNews incomingNews = new IncomingNews();
		incomingNews.add(new IncomingInfo("publiczny", SubsciptionType.NONE));
		incomingNews.add(new IncomingInfo("subA", SubsciptionType.A));
		incomingNews.add(new IncomingInfo("subB", SubsciptionType.B));
		incomingNews.add(new IncomingInfo("subC", SubsciptionType.C));
		NewsReader newsReader = new NewsReader() {

			@Override
			public IncomingNews read() {
				return incomingNews;
			}
		};
		
		Whitebox.setInternalState(testConfiguration, "readerType", readerExample);
		when(ConfigurationLoader.getInstance()).thenReturn(testConfigurationLoader);
        when(testConfigurationLoader.loadConfiguration()).thenReturn(testConfiguration);
        when(NewsReaderFactory.getReader(readerExample)).thenReturn(newsReader);
		
	}

	@Test
	public void testIfPublicNewsIsAddedCorrectly() {
		PublishableNews publishNews = PublishableNews.create();
		publishNews.addPublicInfo("publiczny");
		exampleList = (List<String>) Whitebox.getInternalState(publishNews, "publicContent");
		assertThat(exampleList.size(), is(not(equalTo(0))));
		assertThat(exampleList.get(0), is(equalTo("publiczny")));
	}
	
	@Test
	public void testNewsLoaderTestLoadNewsCheckSubNews(){
		NewsLoader newsLoader = new NewsLoader();
		final PublishableNews publishableNews = getMockOfPublishableNews();
		mockStatic(PublishableNews.class);
		when(PublishableNews.create()).thenReturn(publishableNews);
		PublishableNews publishable = newsLoader.loadNews();
		List<String> result = (List<String>)Whitebox.getInternalState(publishable,"subscribentContent");
		assertThat(result.size(), is(3));
		assertThat(result.get(0),is(equalTo("subA")));
	}
	
	private PublishableNews getMockOfPublishableNews() {
		return new PublishableNews() {
			private final List<String> subscribentContent = new ArrayList<>();
			
			@Override
			public void addForSubscription(String content, SubsciptionType subscriptionType) {
				this.subscribentContent.add(content);
			}
		};
	}
}
