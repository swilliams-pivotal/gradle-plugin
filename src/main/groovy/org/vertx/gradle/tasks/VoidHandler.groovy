package org.vertx.gradle.tasks

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

import org.vertx.java.core.Handler

public class VoidHandler implements Handler<Void> {

  private CountDownLatch latch

  public VoidHandler(int size) {
    this.latch = new CountDownLatch(size)
  }

  @Override
  public void handle(Void event) {
    latch.countDown()
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
