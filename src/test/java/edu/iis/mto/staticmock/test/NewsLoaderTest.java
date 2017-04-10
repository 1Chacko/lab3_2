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
		incomingNews.add(new IncomingInfo("pub", SubsciptionType.NONE));
		incomingNews.add(new IncomingInfo("subA", SubsciptionType.A));
		incomingNews.add(new IncomingInfo("subB", SubsciptionType.B));
		incomingNews.add(new IncomingInfo("subC", SubsciptionType.C));
		NewsReader newsReader = new NewsReader() {

			@Override
			public IncomingNews read() {
				return incomingNews;
			}
		};
		when(NewsReaderFactory.getReader("test")).thenReturn(newsReader);
	}

	@Test
	public void testIfPublicNewsIsAddedCorrectly() {
		PublishableNews publishNews = PublishableNews.create();
		publishNews.addPublicInfo("pub");
		exampleList = (List<String>) Whitebox.getInternalState(publishNews, "publicContent");
		assertThat(exampleList.size(), is(not(equalTo(0))));
		assertThat(exampleList.get(0), is(equalTo("pub")));
	}

}
