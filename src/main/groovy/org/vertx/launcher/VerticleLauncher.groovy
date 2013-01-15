package org.vertx.launcher

import org.vertx.java.core.json.JsonObject
import org.vertx.java.deploy.impl.VerticleManager

class VerticleLauncher extends AbstractVertxLauncher {

  boolean worker

  URL[] urls

  String includes

  @Override
  protected void action() throws Exception {
    VerticleManager manager = getManager()
    manager.deployVerticle(worker, main, config, urls, instances, modDir, includes, handler)
  }

}
