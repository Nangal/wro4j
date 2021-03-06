package ro.isdc.wro.extensions.processor.js;

import org.apache.commons.io.FilenameUtils;

import ro.isdc.wro.extensions.processor.support.emberjs.EmberJs;
import ro.isdc.wro.extensions.processor.support.template.AbstractJsTemplateCompiler;
import ro.isdc.wro.model.resource.Resource;
import ro.isdc.wro.model.resource.ResourceType;
import ro.isdc.wro.model.resource.SupportedResourceType;


/**
 * Compiles EmberJS templates to javascript. The processor loads emberJs library and all its dependencies from the webjar.
 *
 * @author blemoine
 */
@SupportedResourceType(ResourceType.JS)
public class EmberJsProcessor
    extends JsTemplateCompilerProcessor {

  public static final String ALIAS = "emberJs";

  /**
   * {@inheritDoc}
   */
  @Override
  protected AbstractJsTemplateCompiler createCompiler() {
    return new EmberJs();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getArgument(final Resource resource) {
    final String name = resource == null ? "" : FilenameUtils.getBaseName(resource.getUri());
    return String.format("'%s'", name);
  }
}
