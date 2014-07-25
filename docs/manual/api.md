[< back to Manual](../manual.md)

#Alchemy REST API

The `alchemy-service` module provides you with a baseline dropwizard service, that given an appropriate configuration, will host a RESTful API that can be used to interact with experiments.

- Endpoints
  * [Experiments](#experiments)
  * [Treatments](#treatments)
  * [Allocations](#allocations)
  * [Treatment Overrides](#treatment_overrides)
  * [Active Treatments](#active_treatments)
  * [Metadata](#metadata)

<a name="experiments"></a>
###Experiments

`GET /experiments`

**Response:** *200 OK*

**Example Response:**

```json
    [
        {
            "name": "pie_vs_cake",
            "description": "My experiment",
            "seed": 123,
            "filter": [ "identified_user" ],
            "hashAttributes": ["user_name"],
            "active": true,
            "created": 1398347307568,
            "modified": 1398347517914,
            "activated": 1398347307553,
            "deactivated": null,
            "treatments": [
                {
                    "name": "cake",
                    "description": "people want cake"
                },
                {
                    "name": "pie",
                    "description": "people want pie"
                },
                {
                    "name": "control",
                    "description": "the default case"
                }
            ],
            "allocations": [
                {
                    "treatment": "cake",
                    "offset": 0,
                    "size": 40
                },
                {
                    "treatment": "pie",
                    "offset": 40,
                    "size": 40
                }
            ],
            "overrides": [
                {
                    "name": "qa_cake",
                    "treatment": "cake",
                    "filter": "user_name=\"qa\""
                }
            ]
        }
    ]
```

`GET /experiments/{experimentName}`

**Response:** *404 Not Found* if experiment not found, otherwise *200 OK*

**Example Response:**

```json
    {
        "name": "pie_vs_cake",
        "description": "My experiment",
        "seed": 123,
        "filter": ["identified_user"],
        "hashAttributes": ["user_name"],
        "active": true,
        "created": 1398347307568,
        "modified": 1398347517914,
        "activated": 1398347307553,
        "deactivated": null,
        "treatments": [
            {
                "name": "cake",
                "description": "people want cake"
            },
            {
                "name": "pie",
                "description": "people want pie"
            },
            {
                "name": "control",
                "description": "the default case"
            }
        ],
        "allocations": [
            {
                "treatment": "cake",
                "offset": 0,
                "size": 40
            },
            {
                "treatment": "pie",
                "offset": 40,
                "size": 40
            }
        ],
        "overrides": [
            {
                "name": "qa_cake",
                "treatment": "cake",
                "filter": "user_name=\"qa\""
            }
        ]
    }
```

`PUT /experiments`

**Response:** *201 Created*

**Example Payload:**

```json
    {
        "name": "pie_vs_cake",
        "description": "My experiment",
        "seed": 123,
        "filter": [ "identified_user" ],
        "hashAttributes": ["user_name"],
        "active": true,
        "treatments": [
            {
                "name": "control",
                "description": "the default case"
            },
            {
                "name": "cake",
                "description": "people want cake"
            },
            {
                "name": "pie",
                "description": "people want pie"
            }
        ],
        "allocations": [
            {
                "treatment": "control",
                "size": 20
            },
            {
                "treatment": "cake",
                "size": 20
            },
            {
                "treatment": "pie",
                "size": 20
            }
        ],
        "overrides": [
            {
                "name": "qa_cake",
                "treatment": "cake",
                "filter": "user_name=\"qa\""
            }
        ]
    }
```

`POST /experiments/{experimentName}`

**Response:** *404 Not Found* if experiment not found, otherwise *204 No Content*

**Example Payload:**

```json
    {
        "description": "new description",
        "active": false,
        "filter": [ "identified_user" ],
        "hashAttributes": ["user_name"],
        "treatments": [
            {
                "name": "control",
                "description": "changing description"
            },
            {
                "name": "cake",
                "description": "the cake is a lie"
            },
            {
                "name": "pi",
                "description": "3.141"
            }
        ],
        "allocations": [
            {
                "treatment": "control",
                "size": 20
            },
            {
                "treatment": "control",
                "size": 20
            },
            {
                "treatment": "control",
                "size": 20
            }
        ],
        "overrides": [
            {
                "name": "qa_cake",
                "treatment": "cake",
                "filter": "user_name=\"qa\""
            }
        ]
    }
```

`DELETE /experiments/{experimentName}`

**Response:** *404 Not Found* if experiment not found, otherwise *204 No Content*


<a name="treatments"></a>
###Treatments

`GET /experiments/{experimentName}/treatments`

**Response:** *404 Not Found* if experiment not found, otherwise *200 OK*

**Example Response:**

```json
    [
        {
            "name": "cake",
            "description": "the cake is a lie"
        },
        {
            "name": "control",
            "description": "changing description"
        },
        {
            "name": "pi",
            "description": "3.141"
        }
    ]
```

`GET /experiments/{experimentName}/treatments/{treatmentName}`

**Response:** *404 Not Found* if experiment or treatment not found, otherwise *200 OK*

**Example Response:**

```json
    {
        "name": "cake",
        "description": "the cake is a lie"
    }
```

`PUT /experiments/{experimentName}/treatments`

**Response:** *404 Not Found* if experiment not found, otherwise *201 Created*

**Example Payload:**

```json
    {
        "name": "new_treatment",
        "description": "my new treatment"
    }
```

`POST /experiments/{experimentName}/treatments/{treatmentName}`

**Response:** *404 Not Found* if experiment or treatment not found, otherwise *204 No Content*

**Example Payload:**

```json
    {
        "description": "new description"
    }
```

`DELETE /experiments/{experimentName}/treatments`

**Response:** *404 Not Found* if experiment not found, otherwise *204 No Content*

`DELETE /experiments/{experimentName}/treatments/{treatmentName}`

**Response:** *404 Not Found* if experiment or treatment not found, otherwise *204 No Content*


<a name="allocations"></a>
###Allocations

`GET /experiments/{experimentName}/allocations`

**Response:** *404 Not Found* if experiment not found, otherwise *200 OK*

**Example Response:**

```json
    [
        {
            "treatment": "control",
            "offset": 0,
            "size": 20
        },
        {
            "treatment": "cake",
            "offset": 20,
            "size": 20
        },
        {
            "treatment": "pie",
            "offset": 40,
            "size": 20
        }
    ]
```

`POST /experiments/{experimentName}/allocations`

**Response:** *404 Not Found* if experiment not found, otherwise *204 No Content*

**Example Payload:**

```json
    [
      {
        "action": "allocate",
        "treatment": "control",
        "size": 20
      },
      {
        "action": "deallocate",
        "treatment": "cake",
        "size": 10
      },
      {
        "action": "reallocate",
        "treatment": "control",
        "target": "pie",
        "size": 5
      }
    ]
```

`DELETE /experiments/{experimentName}/allocations`

**Response:** *404 Not Found* if experiment not found, otherwise *204 No Content*

<a name="treatment_overrides"></a>
###Treatment Overrides

`GET /experiments/{experimentName}/overrides`

**Response:** *404 Not Found* if experiment not found, otherwise *200 OK*

**Example Response:**

```json
    [
        {
            "name": "qa_cake",
            "treatment": "cake",
            "filter": "user_name=\"qa\""
        }
    ]
```

`GET /experiments/{experimentName}/overrides/{overrideName}`

**Response:** *404 Not Found* if experiment not found, otherwise *200 OK*

**Example Response:**

```json
    {
        "name": "qa_cake",
        "treatment": "cake"
        "filter": "user_name=\"qa\""
    }
```

`PUT /experiments/{experimentName}/overrides`

**Response:** *404 Not Found* if experiment not found, otherwise *201 Created*

**Example Payload:**

```json
    {
        "name": "qa_override",
        "treatment": "cake",
        "filter": "user_name=\"qa\""
    }
```

`DELETE /experiments/{experimentName}/overrides`

**Response:** *404 Not Found* if experiment not found, otherwise *204 No Content*

`DELETE /experiments/{experimentName}/overrides/{overrideName}`

**Response:** *404 Not Found* if experiment or override not found, otherwise *204 No Content*

<a name="active_treatments"></a>
###Active Treatments

`POST /active/experiments/{experimentName}/treatment`

**Response:** *204 Not Content* if no treatment assigned to identity, otherwise *200 OK*

**Example Payload:**

```json
    {
        "type": "user",
        "name": "qa"
    }
```

**Example Response:**

```json
    {
        "name": "pie",
        "description": "people want pie"
    }
```

`POST /active/treatments`

**Response:** *200 OK*

**Example Payload:**

```json
    [
        {
            "type": "user",
            "name": "qa"
        },
        {
            "type": "device",
            "name": "10efb20abe0ff1ec"
        }
    ]
```

**Example Response:**

```json
    {
        "pie_vs_cake": {
            "name": "cake",
            "description": "people want cake"
        }
    }
```
<a name="metadata"></a>
###Metadata

`GET /metadata/identityTypes`

**Response:** *200 OK*

**Example Response:**

```json
    {
        "user": "com.rtr.alchemy.example.dto.UserDto"
    }
```

`GET /metadata/identityTypes/{identityType}/schema`

**Response:** *404* Not Found if type was not found, otherwise *200 OK*

**Example Response:**

```json
    {
        "type": "object",
        "properties": {
            "name": {
                "type": "string"
            }
        }
    }
```

`GET /metadata/identityTypes/{identityType}/attributes`

**Response:** *404* Not Found if type was not found, otherwise *200 OK*

**Example Response:**

```json
    [
        "identified",
        "anonymous"
    ]
```

`GET /metadata/identity/attributes`

**Response:** *404* Not Found if type was not found, otherwise *200 OK*

**Example Payload:**

```json
    {
        "type": "user",
        "name": "qa"
    }
```

**Example Response:**

```json
    {
        "user": true,
        "identified": true,
        "user_name": "qa"
    }
```