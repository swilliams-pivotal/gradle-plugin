package org.vertx.launcher

import java.io.File;

import org.vertx.java.core.Handler
import org.vertx.java.core.Vertx
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.impl.VerticleManager

abstract class AbstractVertxLauncher extends Thread {

  private VerticleManager manager

  String main

  JsonObject config

  int instances

  File modDir

  Handler handler

  AbstractVertxLauncher() { }

  @Override
  public void run() {
    try {
      Vertx vertx = Vertx.newVertx()
      this.manager = new VerticleManager(vertx)  
      action()
    }
    catch (Exception e) {
      e.printStackTrace()
    }
  }

  protected VerticleManager getVerticleManager() {
    manager
  }

  protected abstract void action() throws Exception

}
