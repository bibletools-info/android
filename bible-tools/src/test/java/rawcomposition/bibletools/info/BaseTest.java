package rawcomposition.bibletools.info;

import android.test.mock.MockContext;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rawcomposition.bibletools.info.ui.downloads.util.CacheState;
import rawcomposition.bibletools.info.ui.downloads.util.ResourceCacheManager;

/**
 * Created by tinashe on 2017/04/12.
 */

public class BaseTest {

    @Mock
    MockContext context;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSomething() {

        @CacheState int state = ResourceCacheManager.getInstance()
                .getCacheState(context, "Luke 2");


        Assert.assertEquals(CacheState.EMPTY, state);
    }
}
