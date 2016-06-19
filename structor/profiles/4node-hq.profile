{
  "domain": "local.vm",
  "realm": "LOCAL.VM",
  "hdp": "2.4.0",
  "security": false,
  "clients" : [ "hdfs", "hive", "yarn", "zk" ],
  "shared_folders": [
  ],
  "storm": {
    "version": "1.0.0",
    "ui_port": 8800,
    "supervisor_slots_ports": [ 6700, 6701, 6702, 6703 ]
  },
  "vm_cpus": 4,
  "nodes": [
    {
      "hostname": "nn",
      "ip": "240.0.0.20",
      "vm_mem": 2048,
      "server_mem": 300,
      "client_mem": 200,
      "forwarded_ports": [
        { "guest": 50070, "host": 50070 },
        { "guest": 19888, "host": 19888 },
        { "guest": 9001, "host": 9001 },
        { "guest": 8042, "host": 8042 },
        { "guest": 8088, "host": 8088 },
        { "guest": 8000, "host": 8000 },
        { "guest": 8020, "host": 8020 }
      ],
      "roles": [ "nn", "zk", "yarn", "hive-db", "hive-meta", "hue", "kafka-manager", "storm-nimbus", "client" ]
    },
    {
      "hostname": "stats",
      "ip": "240.0.0.30",
      "vm_cpus": 1,
      "vm_mem": 2048,
      "server_mem": 300,
      "client_mem": 200,
      "forwarded_ports": [
        { "guest": 9090, "host": 9090 },
        { "guest": 9091, "host": 9091 },
	    { "guest": 6379, "host": 6379 },
        { "guest": 3000, "host": 3000 }
      ],
      "roles": [ "grafana", "redis", "zk", "client" ]
    },
    {
      "hostname": "data1",
      "ip": "240.0.0.41",
      "vm_mem": 3072,
      "server_mem": 300,
      "client_mem": 200,
      "forwarded_ports": [
        { "guest": 18080, "host": 18081 }
      ],
      "roles": [ "storm-supervisor", "log_gateway", "kafka", "slave" ]
    },
    {
      "hostname": "data2",
      "ip": "240.0.0.42",
      "vm_mem": 3072,
      "server_mem": 300,
      "client_mem": 200,
      "forwarded_ports": [
        { "guest": 18080, "host": 18082 }
      ],
      "roles": [ "storm-supervisor", "log_gateway", "kafka", "slave" ]
    }
  ]
}
