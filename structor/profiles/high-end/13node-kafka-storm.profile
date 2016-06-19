{
  "domain": "local.vm",
  "realm": "LOCAL.VM",
  "hdp": "2.2.6",
  "security": false,
  "svn_user": "orioll",
  "clients" : [ "hdfs", "yarn", "zk", "hbase" ],
  "mail": "oriol.lopez@beabloo.com",
  "shared_folders": [
    { "guest": "/localrepo", "host": "~/Repositarios/" }
  ],
  "local_repo_path": "/localrepo",
  "storm": {
    "ui_port": 8800,
    "supervisor_slots_ports": [ 6700, 6701, 6702, 6703 ]
  },
  "vm_cpus": 4,
  "nodes": [
    {
      "hostname": "nn",
      "ip": "240.0.0.20",
      "vm_mem": 8192,
      "server_mem": 300,
      "client_mem": 200,
      "forwarded_ports": [
        { "guest": 60010, "host": 60010 },
        { "guest": 50070, "host": 50070 },
        { "guest": 19888, "host": 19888 },
        { "guest": 8088, "host": 8088 },
        { "guest": 8000, "host": 8000 },
        { "guest": 8800, "host": 8800 },
        { "guest": 8020, "host": 8020 }
      ],
      "roles": [ "nn", "zk", "yarn", "storm-nimbus", "hbase-master", "client" ]
    },
    {
      "hostname": "data1",
      "ip": "240.0.0.30",
      "vm_mem": 4096,
      "server_mem": 300,
      "client_mem": 200,
      "forwarded_ports": [
        { "guest": 60030, "host": 60030 }
      ],
      "roles": [ "hbase-regionserver", "slave" ]
    },
    {
      "hostname": "data2",
      "ip": "240.0.0.31",
      "vm_mem": 4096,
      "server_mem": 300,
      "client_mem": 200,
      "forwarded_ports": [
        { "guest": 60030, "host": 60030 }
      ],
      "roles": [ "hbase-regionserver", "slave" ]
    },
    {
      "hostname": "data3",
      "ip": "240.0.0.32",
      "vm_mem": 4096,
      "server_mem": 300,
      "client_mem": 200,
      "forwarded_ports": [
        { "guest": 60030, "host": 60030 }
      ],
      "roles": [ "hbase-regionserver", "slave" ]
    },
    {
      "hostname": "data4",
      "ip": "240.0.0.33",
      "vm_mem": 4096,
      "server_mem": 300,
      "client_mem": 200,
      "forwarded_ports": [
        { "guest": 60030, "host": 60030 }
      ],
      "roles": [ "hbase-regionserver", "slave" ]
    },
    {
      "hostname": "kafka1",
      "ip": "240.0.0.40",
      "vm_cpus": 1,
      "vm_mem": 2048,
      "server_mem": 300,
      "client_mem": 200,
      "forwarded_ports": [
      ],
      "roles": [ "kafka" ]
    },
    {
      "hostname": "kafka2",
      "ip": "240.0.0.41",
      "vm_cpus": 1,
      "vm_mem": 2048,
      "server_mem": 300,
      "client_mem": 200,
      "forwarded_ports": [
      ],
      "roles": [ "kafka" ]
    },
    {
      "hostname": "kafka3",
      "ip": "240.0.0.42",
      "vm_cpus": 1,
      "vm_mem": 2048,
      "server_mem": 300,
      "client_mem": 200,
      "forwarded_ports": [
      ],
      "roles": [ "kafka" ]
    },
    {
      "hostname": "kafka4",
      "ip": "240.0.0.43",
      "vm_cpus": 1,
      "vm_mem": 2048,
      "server_mem": 300,
      "client_mem": 200,
      "forwarded_ports": [
      ],
      "roles": [ "kafka" ]
    },
    {
      "hostname": "storm1",
      "ip": "240.0.0.50",
      "vm_cpus": 1,
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
      "vm_cpus": 1,
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
      "vm_cpus": 1,
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
      "vm_cpus": 1,
      "vm_mem": 2048,
      "server_mem": 300,
      "client_mem": 200,
      "forwarded_ports": [
      ],
      "roles": [ "storm-supervisor" ]
    }
  ]
}