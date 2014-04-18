.. _man-rest-api:

################################
Alchemy REST API
################################

.. highlight:: text

.. rubric:: The ``alchemy-service`` module provides you with a baseline dropwizard service, that given an appropriate configuration, will host a RESTful API that can be used to interact with experiments.

These are the currently implemented endpoints:

Experiments
-----------

``GET /experiments``

**Response:** *200 OK*

**Example Response:**

.. code-block:: json

    [
        {
            "name": "pie_vs_cake",
            "description": "My experiment",
            "identityType": "user",
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
                    "treatment": "cake"
                }
            ]
        }
    ]

|
|
|

``GET /experiments/{experimentName}``

**Response:** *404 Not Found* if experiment not found, otherwise *200 OK*

**Example Response:**

.. code-block:: json

    {
        "name": "pie_vs_cake",
        "description": "My experiment",
        "identityType": "user",
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
                "treatment": "cake"
            }
        ]
    }

|
|
|

``PUT /experiments``

**Response:** *201 Created*

**Example Payload:**

.. code-block:: json

    {
        "name": "pie_vs_cake",
        "description": "My experiment",
        "identityType": "user",
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
                "identity": {
                    "type": "user",
                    "name": "qa"
                }
            }
        ]
    }

|
|
|

``POST /experiments/{experimentName}``

**Response:** *404 Not Found* if experiment not found, otherwise *204 No Content*

**Example Payload:**

.. code-block:: json

    {
        "description": "new description",
        "active": false,
        "identityType": "user",
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
                "identity": {
                    "type": "user",
                    "name": "qa"
                }
            }
        ]
    }

|
|
|

``DELETE /experiments/{experimentName}``

**Response:** *404 Not Found* if experiment not found, otherwise *204 No Content*

|
|
|

Treatments
----------

``GET /experiments/{experimentName}/treatments``

**Response:** *404 Not Found* if experiment not found, otherwise *200 OK*

**Example Response:**

.. code-block:: json

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

|
|
|

``GET /experiments/{experimentName}/treatments/{treatmentName}``

**Response:** *404 Not Found* if experiment or treatment not found, otherwise *200 OK*

**Example Response:**

.. code-block:: json

    {
        "name": "cake",
        "description": "the cake is a lie"
    }

|
|
|

``PUT /experiments/{experimentName}/treatments``

**Response:** *404 Not Found* if experiment not found, otherwise *201 Created*

**Example Payload:**

.. code-block:: json

    {
        "name": "new_treatment",
        "description": "my new treatment"
    }

|
|
|

``DELETE /experiments/{experimentName}/treatments``

**Response:** *404 Not Found* if experiment not found, otherwise *204 No Content*

|
|
|

``DELETE /experiments/{experimentName}/treatments/{treatmentName}``

**Response:** *404 Not Found* if experiment or treatment not found, otherwise *204 No Content*

|
|
|

Allocations
-----------

``GET /experiments/{experimentName}/allocations``

**Response:** *404 Not Found* if experiment not found, otherwise *200 OK*

**Example Response:**

.. code-block:: json

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

|
|
|

``POST /experiments/{experimentName}/allocations``

**Response:** *404 Not Found* if experiment not found, otherwise *204 No Content*

**Example Payload:**

.. code-block:: json

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

|
|
|

``DELETE /experiments/{experimentName}/allocations``

**Response:** *404 Not Found* if experiment not found, otherwise *204 No Content*

|
|
|

Treatment Overrides
-------------------

``GET /experiments/{experimentName}/overrides``

**Response:** *404 Not Found* if experiment not found, otherwise *200 OK*

**Example Response:**

.. code-block:: json

    [
        {
            "name": "qa_cake",
            "treatment": "cake"
        }
    ]

|
|
|

``GET /experiments/{experimentName}/overrides/{overrideName}``

**Response:** *404 Not Found* if experiment not found, otherwise *200 OK*

**Example Response:**

.. code-block:: json

    {
        "name": "qa_cake",
        "treatment": "cake"
    }

|
|
|

``PUT /experiments/{experimentName}/overrides``

**Response:** *404 Not Found* if experiment not found, otherwise *201 Created*

**Example Payload:**

.. code-block:: json

    {
        "name": "qa_override",
        "treatment": "cake",
        "identity": {
            "type": "user",
            "name": "qa"
        }
    }

|
|
|

``DELETE /experiments/{experimentName}/overrides``

**Response:** *404 Not Found* if experiment not found, otherwise *204 No Content*

|
|
|

``DELETE /experiments/{experimentName}/overrides/{overrideName}``

**Response:** *404 Not Found* if experiment or override not found, otherwise *204 No Content*

|
|
|

Active Treatments
-----------------

``POST /active/experiments/{experimentName}/treatment``

**Response:** *204 Not Content* if no treatment assigned to identity, otherwise *200 OK*

**Example Payload:**

.. code-block:: json

    {
        "type": "user",
        "name": "qa"
    }

**Example Response:**

.. code-block:: json

    {
        "name": "pie",
        "description": "people want pie"
    }

|
|
|

``POST /active/treatments``

**Response:** *200 OK*

**Example Payload:**

.. code-block:: json

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

**Example Response:**

.. code-block:: json

    {
        "pie_vs_cake": {
            "name": "cake",
            "description": "people want cake"
        }
    }
