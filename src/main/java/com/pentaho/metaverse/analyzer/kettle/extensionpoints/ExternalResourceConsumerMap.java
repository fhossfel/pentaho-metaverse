/*!
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2002 - 2014 Pentaho Corporation (Pentaho). All rights reserved.
 *
 * NOTICE: All information including source code contained herein is, and
 * remains the sole property of Pentaho and its licensors. The intellectual
 * and technical concepts contained herein are proprietary and confidential
 * to, and are trade secrets of Pentaho and may be covered by U.S. and foreign
 * patents, or patents in process, and are protected by trade secret and
 * copyright laws. The receipt or possession of this source code and/or related
 * information does not convey or imply any rights to reproduce, disclose or
 * distribute its contents, or to manufacture, use, or sell anything that it
 * may describe, in whole or in part. Any reproduction, modification, distribution,
 * or public display of this information without the express written authorization
 * from Pentaho is strictly prohibited and in violation of applicable laws and
 * international treaties. Access to the source code contained herein is strictly
 * prohibited to anyone except those individuals and entities who have executed
 * confidentiality and non-disclosure agreements or other agreements with Pentaho,
 * explicitly covering such access.
 */
package com.pentaho.metaverse.analyzer.kettle.extensionpoints;

import org.pentaho.di.job.entry.JobEntryBase;
import org.pentaho.di.trans.step.BaseStepMeta;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class maintains a map of StepMeta classes to lists of ExternalResourceConsumers for the purposes
 * of fast lookups to record external resources being used by steps
 */
public class ExternalResourceConsumerMap {

  private static ExternalResourceConsumerMap INSTANCE = new ExternalResourceConsumerMap();

  private final Map<Class<? extends BaseStepMeta>, Queue<IStepExternalResourceConsumer>> stepConsumerMap;

  private final Map<Class<? extends JobEntryBase>, Queue<IJobEntryExternalResourceConsumer>> jobEntryConsumerMap;

  private ExternalResourceConsumerMap() {
    stepConsumerMap =
      new ConcurrentHashMap<Class<? extends BaseStepMeta>, Queue<IStepExternalResourceConsumer>>();
    jobEntryConsumerMap =
      new ConcurrentHashMap<Class<? extends JobEntryBase>, Queue<IJobEntryExternalResourceConsumer>>();
  }

  public static ExternalResourceConsumerMap getInstance() {
    return INSTANCE;
  }

  public synchronized Queue<IStepExternalResourceConsumer> getStepExternalResourceConsumers(
    Class<? extends BaseStepMeta> stepMetaClass ) {
    Queue<IStepExternalResourceConsumer> consumers = stepConsumerMap.get( stepMetaClass );
    if ( consumers == null ) {
      consumers = new ConcurrentLinkedQueue<IStepExternalResourceConsumer>();
      stepConsumerMap.put( stepMetaClass, consumers );
    }
    return consumers;
  }

  public synchronized Queue<IJobEntryExternalResourceConsumer> getJobEntryExternalResourceConsumers(
    Class<? extends JobEntryBase> jobMetaClass ) {
    Queue<IJobEntryExternalResourceConsumer> consumers = jobEntryConsumerMap.get( jobMetaClass );
    if ( consumers == null ) {
      consumers = new ConcurrentLinkedQueue<IJobEntryExternalResourceConsumer>();
      jobEntryConsumerMap.put( jobMetaClass, consumers );
    }
    return consumers;
  }

  public Map<Class<? extends BaseStepMeta>, Queue<IStepExternalResourceConsumer>> getStepConsumerMap() {
    return stepConsumerMap;
  }

  public Map<Class<? extends JobEntryBase>, Queue<IJobEntryExternalResourceConsumer>> getJobEntryConsumerMap() {
    return jobEntryConsumerMap;
  }
}
