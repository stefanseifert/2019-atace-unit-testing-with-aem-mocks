package aemmocksample.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.day.cq.wcm.api.Page;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
class NavigationListTest {

  private AemContext context = new AemContext();

  @BeforeEach
  void setUp() {
    // it may be required to register the models of this project manually depending on your project/IDE setup
    // context.addModelsForClasses(NavigationList.class);
  }

  @Test
  void testGetChildren() {

    // create a root page with some child pages
    Page rootPage = context.create().page("/content/mysite");

    context.create().page("/content/mysite/page1");
    context.create().page("/content/mysite/page2", null,
        "hideInNav", true);
    context.create().page("/content/mysite/page3");

    // root page is current page
    context.currentPage(rootPage);

    // simulate a parameter passed in from HTL template to the model
    context.request().setAttribute("limit", 2);

    // instantiate the model
    NavigationList underTest = context.request().adaptTo(NavigationList.class);

    // assert the result
    List<Page> result = underTest.getChildren();
    assertEquals(2, result.size());
    assertEquals("/content/mysite/page1", result.get(0).getPath());
    assertEquals("/content/mysite/page3", result.get(1).getPath());

  }

}
