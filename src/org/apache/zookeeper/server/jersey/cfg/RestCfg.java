/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.zookeeper.server.jersey.cfg;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class RestCfg {

   private Properties cfg = new Properties();

   private Set<Endpoint> endpoints = new HashSet<Endpoint>();
   private Credentials credentials = new Credentials();

   public RestCfg(String resource) throws IOException {
       this(RestCfg.class.getClassLoader().getResourceAsStream(resource));
   }

   public RestCfg(InputStream io) throws IOException {
       cfg.load(io);
       extractEndpoints();
       extractCredentials();
       //If endpoint not defined explicitely use local ZK client configuration
       if (this.getEndpoints().size() == 0) {
           Endpoint point = new Endpoint("/", this.getZooClientPortAddress() + ":" + this.getZooClientPort());
           point.setCredentials("");
           point.setZooKeeperAuthInfo("");
           this.getEndpoints().add(point);
       }
       
   }

   private void extractCredentials() {
       if (cfg.containsKey("rest.http.auth")) {
           credentials = new Credentials(cfg.getProperty("rest.http.auth", ""));
       }
   }

   private void extractEndpoints() {
       int count = 1;
       while (true) {
           String e = cfg.getProperty(
                   String.format("rest.endpoint.%d", count), null);
           if (e == null) {
               break;
           }

           String[] parts = e.split(";");
           if (parts.length != 2) {
               count++;
               continue;
           }
           Endpoint point = new Endpoint(parts[0], parts[1]);
           
           String c = cfg.getProperty(String.format(
                   "rest.endpoint.%d.http.auth", count), "");
           point.setCredentials(c);
           
           String digest = cfg.getProperty(String.format(
                   "rest.endpoint.%d.zk.digest", count), "");
           point.setZooKeeperAuthInfo(digest);

           endpoints.add(point);
           count++;
       }
   }
   
   
   
   public String formatEndpoints() {
       StringBuilder result = new StringBuilder();
       for (Endpoint e : this.getEndpoints()) {
           String context = e.getContext();
           if (context.charAt(context.length() - 1) != '/') {
               context += "/";
           }
           result.append(String.format("Started %s - WADL: http://localhost:%d%sapplication.wadl",
                   context, this.getPort(), context));
       }
       return result.toString();
   }
   
   public int getZooClientPort() {
       return Integer.parseInt(cfg.getProperty("clientPort", "2181"));
   }
   
   public String getZooClientPortAddress() {
       return cfg.getProperty("clientPortAddress", "127.0.0.1");
   }

   public int getPort() {
       return Integer.parseInt(cfg.getProperty("rest.port", "9998"));
   }
   
   public String getBindHost() {
       return cfg.getProperty("rest.host", null);
   }

   public boolean useSSL() {
       return Boolean.valueOf(cfg.getProperty("rest.ssl", "false"));
   }

   public final Set<Endpoint> getEndpoints() {
       return endpoints;
   }

   public final Credentials getCredentials() {
       return credentials;
   }

   public String getJKS() {
       return cfg.getProperty("rest.ssl.jks");
   }

   public String getJKS(String def) {
       return cfg.getProperty("rest.ssl.jks", def);
   }

   public String getJKSPassword() {
       return cfg.getProperty("rest.ssl.jks.pass");
   }
}
