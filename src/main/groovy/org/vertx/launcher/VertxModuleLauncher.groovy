package org.vertx.launcher

import org.vertx.java.core.json.JsonObject
import org.vertx.java.deploy.impl.VerticleManager

class VertxModuleLauncher extends AbstractVertxLauncher {

  @Override
  protected void action() throws Exception {
    VerticleManager manager = getManager()
    manager.deployMod(main, config, instances, modDir, handler)
  }

}
