{
  "domain": "bb-dev.com",
  "realm": "BB-DEV.COM",
  "security": false,
  "svn_user": "",
  "clients" : [ "hdfs", "hive", "oozie", "pig", "tez", "yarn", "zk" ],
  "mail": "devbigdata@beabloo.com",
  "shared_folders": [
    { "guest": "/localrepo", "host": "" }
  ],
  "local_repo_path": "/localrepo",
  "vm_cpus": 4,
  "nodes": [
    {
      "hostname": "nn",
      "ip": "240.0.0.21",
      "vm_mem": 2048,
      "server_mem": 300,
      "client_mem": 200,
      "forwarded_ports": [
        { "guest": 50070, "host": 50070 },
        { "guest": 19888, "host": 19888 },
        { "guest": 8088, "host": 8088 },
        { "guest": 8000, "host": 8000 }
      ],
      "roles": [ "nn", "zk", "yarn", "hive-db", "hive-meta", "hue", "client" ]
    },
    {
      "hostname": "data1",
      "ip": "240.0.0.22",
      "vm_mem": 3072,
      "server_mem": 300,
      "client_mem": 200,
      "forwarded_ports": [
        { "guest": 8042, "host": 8042 }
      ],
      "roles": [ "slave" ]
    },
    {
      "hostname": "data2",
      "ip": "240.0.0.23",
      "vm_mem": 3072,
      "server_mem": 300,
      "client_mem": 200,
      "forwarded_ports": [
        { "guest": 8042, "host": 8042 }
      ],
      "roles": [ "slave" ]
    },
    {
      "hostname": "jt",
      "ip": "240.0.0.20",
      "vm_cpus": 1,
      "vm_mem": 1024,
      "server_mem": 300,
      "client_mem": 200,
      "forwarded_ports": [
        { "guest": 11000, "host": 11000 },
        { "guest": 60030, "host": 60031 }
      ],
      "roles": [ "oozie", "beabloo_deployer_local", "client" ]
    }
  ]
}
