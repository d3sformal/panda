package gov.nasa.jpf.abstraction.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPFConfigException;
import gov.nasa.jpf.JPFException;
import gov.nasa.jpf.vm.DebugStateSerializer;
import gov.nasa.jpf.vm.JenkinsStateSet;
import gov.nasa.jpf.vm.VM;

public class DebugCopyPreservingStateSet extends JenkinsStateSet {

  static final String LOGFILE = "state";

  private Map<Integer, Integer> visits = new HashMap<Integer, Integer>();
  protected File outputDir;
  protected File outputFile;

  public int visit(int stateId) {
      if (!visits.containsKey(stateId)) {
          visits.put(stateId, 0);
      }

      int visit = visits.get(stateId);

      visits.put(stateId, visit + 1);

      return visit;
  }

  public DebugCopyPreservingStateSet(Config conf){
    String serializerCls = conf.getString("vm.serializer.class");

    if (serializerCls == null) {
        serializerCls = "gov.nasa.jpf.abstraction.util.DebugPredicateAbstractionSerializer";
    }

    try {
        serializer = conf.getInstance(null, serializerCls, DebugStateSerializer.class);
    } catch (Throwable t) {
        throw new JPFConfigException("Debug StateSet cannot instantiate debug serializer: " + serializerCls, t);
    }

    String path = conf.getString("vm.serializer.output", "tmp");
    outputDir = new File(path);
    if (!outputDir.isDirectory()){
      if (!outputDir.mkdirs()){
        throw new JPFConfigException("Debug StateSet cannot create output dir: " + outputDir.getAbsolutePath());
      }
    }

    outputFile = new File( outputDir, LOGFILE);
  }

  @Override
  public void attach(VM vm){
    vm.setSerializer( serializer);

    super.attach(vm);
  }

  @Override
  public int addCurrent() {
    int maxId = size() - 1;
    FileOutputStream fos = null;

    try {
      fos = new FileOutputStream(outputFile);
    } catch (FileNotFoundException fnfx){
      throw new JPFException("cannot create Debug state set output file: " + outputFile.getAbsolutePath());
    }

    ((DebugStateSerializer)serializer).setOutputStream(fos);

    int stateId = super.addCurrent();

    try {
      fos.flush();
      fos.close();
    } catch (IOException iox){
      throw new JPFException("cannot write Debug state set output file: " + outputFile.getAbsolutePath());
    }

    String fname = "state." + stateId + "." + visit(stateId);
    outputFile.renameTo( new File(outputDir, fname));

    return stateId;
  }
}
