.. _man-core:

###############
Alchemy Core
###############

.. highlight:: text

.. rubric:: The ``alchemy-core`` module provides you with everything you'll need to write A/B
            experiments applications.

It includes models for:

* ``Experiment`` - A collection of treatments which are assigned via an allocation and identity
* ``Treatment`` - Represents a possible user experience in an experiment
* ``Allocation`` - Represents a contiguous allocation block of a single treatment in an experiment
* ``Allocations`` - A collection of Allocation
* ``Identity`` - Requests are assigned to an allocation via an identity, which generates a uniq hash for a request which is used for treatment allocation


Identities that exist
======================
All Indentity generate a hash. What varies from identity to identity are the elements being hashed. The list of items included the hash are listed below. Common to all is a seeding integer, which provides for generating a more random assignemnt across experiments & treatments.

* ``Device`` - [deviceId]
* ``GeoLocation`` - [country,region,city,zip,lat,long,metroCode,areaCode]
* ``User`` - [userId]

Allocation Info
=================

