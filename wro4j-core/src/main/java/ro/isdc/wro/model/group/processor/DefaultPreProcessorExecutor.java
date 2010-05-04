package ro.isdc.wro.model.group.processor;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.isdc.wro.model.resource.Resource;
import ro.isdc.wro.model.resource.processor.PreProcessorExecutor;
import ro.isdc.wro.model.resource.processor.ResourcePreProcessor;


/**
 * Default implementation of {@link PreProcessorExecutor}.
 */
class DefaultPreProcessorExecutor
    implements PreProcessorExecutor {
  private static final Logger LOG = LoggerFactory.getLogger(DefaultPreProcessorExecutor.class);
  private final AbstractGroupsProcessor groupsProcessor;


  /**
   * Constructor.
   *
   * @param groupsProcessor
   *          to set.
   */
  public DefaultPreProcessorExecutor(final AbstractGroupsProcessor groupsProcessor) {
    this.groupsProcessor = groupsProcessor;
  }


  /**
   * {@inheritDoc}
   */
  public String execute(final Resource resource, final boolean minimize)
      throws IOException {
    if (resource == null) {
      throw new IllegalArgumentException("Resource cannot be null!");
    }
    // merge preProcessorsBy type and anyPreProcessors
    final Collection<ResourcePreProcessor> processors = groupsProcessor.getPreProcessorsByType(resource.getType());
    processors.addAll(groupsProcessor.getPreProcessorsByType(null));
    if (!minimize) {
      GroupsProcessorImpl.removeMinimizeAwareProcessors(processors);
    }
    final Writer output = applyPreProcessors(processors, resource);
    return output.toString();
  }


  /**
   * TODO optimize <br/>
   * Apply a list of preprocessors on a resource.
   *
   * @throws IOException
   *           if any IO error occurs while processing.
   */
  private Writer applyPreProcessors(final Collection<ResourcePreProcessor> processors, final Resource resource)
      throws IOException {
    // get original content
    Reader reader = null;
    Writer output = new StringWriter();
    try {
      reader = groupsProcessor.getResourceReader(resource);
    } catch (final IOException e) {
      if (groupsProcessor.isIgnoreMissingResources()) {
        LOG.warn("Invalid resource found: " + resource);
        return output;
      } else {
        throw e;
      }
    }
    if (processors.isEmpty()) {
      IOUtils.copy(reader, output);
      return output;
    }
    Reader input = reader;
    for (final ResourcePreProcessor processor : processors) {
      output = new StringWriter();
      LOG.debug("applying preProcessor: " + processor.getClass().getName());
      processor.process(resource, input, output);
      input = new StringReader(output.toString());
    }
    return output;
  }
}