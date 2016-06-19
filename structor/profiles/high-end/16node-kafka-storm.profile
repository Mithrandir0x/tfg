{
  "domain": "local.vm",
  "realm": "LOCAL.VM",
  "hdp": "2.4.0",
  "security": false,
  "svn_user": "orioll",
  "clients" : [ "hdfs", "yarn", "zk", "hbase", "hive", "tez" ],
  "mail": "oriol.lopez@beabloo.com",
  "shared_folders": [
    { "guest": "/localrepo", "host": "~/Repositarios/" }
  ],
  "local_repo_path": "/localrepo",
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
      "vm_mem": 4096,
      "server_mem": 300,
      "client_mem": 200,
      "forwarded_ports": [
        { "guest": 50070, "host": 50070 },
        { "guest": 19888, "host": 19888 },
        { "guest": 8088, "host": 8088 },
        { "guest": 8000, "host": 8000 },
        { "guest": 8020, "host": 8020 }
      ],
      "roles": [ "nn", "zk", "yarn", "hue", "client", "kafka-manager" ]
    },
    {
      "hostname": "hive",
      "ip": "240.0.0.21",
      "vm_mem": 4096,
      "server_mem": 300,
      "client_mem": 200,
      "forwarded_ports": [
      ],
      "roles": [ "zk", "hive-db", "hive-meta", "client" ]
    },
    {
      "hostname": "nimbus",
      "ip": "240.0.0.22",
      "vm_mem": 4096,
      "server_mem": 300,
      "client_mem": 200,
      "forwarded_ports": [
        { "guest": 8800, "host": 8800 }
      ],
      "roles": [ "zk", "storm-nimbus", "client" ]
    },
    {
      "hostname": "hbase",
      "ip": "240.0.0.23",
      "vm_mem": 4096,
      "server_mem": 300,
      "client_mem": 200,
      "forwarded_ports": [
        { "guest": 16010, "host": 16010 }
      ],
      "roles": [ "zk", "hbase-master", "client" ]
    },
    {
      "hostname": "data1",
      "ip": "240.0.0.30",
      "vm_mem": 6144,
      "server_mem": 300,
      "client_mem": 200,
      "forwarded_ports": [
        { "guest": 16030, "host": 16030 }
      ],
      "roles": [ "hbase-regionserver", "slave" ]
    },
    {
      "hostname": "data2",
      "ip": "240.0.0.31",
      "vm_mem": 6144,
      "server_mem": 300,
      "client_mem": 200,
      "forwarded_ports": [
        { "guest": 16030, "host": 16030 }
      ],
      "roles": [ "hbase-regionserver", "slave" ]
    },
    {
      "hostname": "data3",
      "ip": "240.0.0.32",
      "vm_mem": 6144,
      "server_mem": 300,
      "client_mem": 200,
      "forwarded_ports": [
        { "guest": 16030, "host": 16030 }
      ],
      "roles": [ "hbase-regionserver", "slave" ]
    },
    {
      "hostname": "data4",
      "ip": "240.0.0.33",
      "vm_mem": 6144,
      "server_mem": 300,
      "client_mem": 200,
      "forwarded_ports": [
        { "guest": 16030, "host": 16030 }
      ],
      "roles": [ "hbase-regionserver", "slave" ]
    },
    {
      "hostname": "kafka1",
      "ip": "240.0.0.40",
      "vm_mem": 2048,
      "server_mem": 300,
      "client_mem": 200,
      "forwarded_ports": [
        { "guest": 18080, "host": 18080 }
      ],
      "roles": [ "log_gateway", "kafka" ]
    },
    {
      "hostname": "kafka2",
      "ip": "240.0.0.41",
      "vm_mem": 2048,
      "server_mem": 300,
      "client_mem": 200,
      "forwarded_ports": [
        { "guest": 18080, "host": 18080 }
      ],
      "roles": [ "log_gateway", "kafka" ]
    },
    {
      "hostname": "kafka3",
      "ip": "240.0.0.42",
      "vm_mem": 2048,
      "server_mem": 300,
      "client_mem": 200,
      "forwarded_ports": [
        { "guest": 18080, "host": 18080 }
      ],
      "roles": [ "log_gateway", "kafka" ]
    },
    {
      "hostname": "kafka4",
      "ip": "240.0.0.43",
      "vm_mem": 2048,
      "server_mem": 300,
      "client_mem": 200,
      "forwarded_ports": [
        { "guest": 18080, "host": 18080 }
      ],
      "roles": [ "log_gateway", "kafka" ]
    },
    {
      "hostname": "storm1",
      "ip": "240.0.0.50",
      "vm_mem": 2048,
      "server_mem": 300,
      "client_mem": 200,
      "forwarded_ports": [
      ],
      "roles": [ "storm-supervisor" ]
    },
    {
      "hostname": "storm2",
      "ip": "240.0.0.51",
      "vm_mem": 2048,
      "server_mem": 300,
      "client_mem": 200,
      "forwarded_ports": [
      ],
      "roles": [ "storm-supervisor" ]
    },
    {
      "hostname": "storm3",
      "ip": "240.0.0.52",
      "vm_mem": 2048,
      "server_mem": 300,
      "client_mem": 200,
      "forwarded_ports": [
      ],
      "roles": [ "storm-supervisor" ]
    },
    {
      "hostname": "storm4",
      "ip": "240.0.0.53",
      "vm_mem": 2048,
      "server_mem": 300,
      "client_mem": 200,
      "forwarded_ports": [
      ],
      "roles": [ "storm-supervisor" ]
    },
    {
      "hostname": "jt",
      "ip": "240.0.0.24",
      "vm_mem": 512,
      "server_mem": 300,
      "client_mem": 200,
      "forwarded_ports": [
        { "guest": 11000, "host": 11000 }
      ],
      "roles": [ "oozie", "beabloo_deployer_local", "client" ]
    }
  ]
}
