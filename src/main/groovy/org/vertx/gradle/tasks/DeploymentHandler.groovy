package org.vertx.gradle.tasks

import java.util.Collections
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

import org.vertx.java.core.Handler

public class DeploymentHandler implements Handler<String> {

  private CountDownLatch latch

  private Set<String> deployments

  public DeploymentHandler(int size) {
    this.latch = new CountDownLatch(size)
    this.deployments = new HashSet<>(size)
  }

  @Override
  public void handle(String event) {
    deployments.add event
    latch.countDown()
  }

  public Set<String> getDeployments() {
    return Collections.unmodifiableSet(deployments)
  }

  public void await() {
    try {
      latch.await()
    } catch (InterruptedException e) {
      e.printStackTrace()
    }
  }

  public boolean await(long timeout, TimeUnit unit) {
    try {
      return latch.await(timeout, unit)
    } catch (InterruptedException e) {
      e.printStackTrace()
    }
    return false
  }

}
