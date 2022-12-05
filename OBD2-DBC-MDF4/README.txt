### OBD2 DBC ###

This OBD2 DBC demo contains most of the standardized OBD2 PIDs from Mode 01.
It does not contain proprietary PIDs, though you can freely expand the DBC as you see fit.

Note that the OBD2 DBC uses complex multiplexing to achieve conversion of the data. 
This follows the DBC standard, but few tools support it.

You can, however, use it with the asammdf GUI/API, which works with the CANedge series.
This lets you directly convert raw OBD2 data logged as MDF4 files from the CANedge - into human-readable form.
In the asammdf GUI you can then e.g. easily plot the data.

Note: The zip contains two DBC file versions. The regular, CSS-Electronics-OBD2-v1.4.dbc, is used with practically all 
cars (to decode 11-bit responses, i.e. 7E8), while the Extended variant is for e.g. vans and heavy duty vehicles, i.e.
decoding the PGN 0xDA00 (55808). See also the raw log file samples for each case.

-----------------------------------------------------------------------
### RELEVANT LINKS ### 

- CANedge series: https://www.csselectronics.com/
- CANedge MDF4 log file tools: https://canlogger.csselectronics.com/canedge-getting-started/log-file-tools/
- How to log OBD2 data: https://www.csselectronics.com/pages/obd2-data-logger-sd-memory-convert
- Contact us: https://www.csselectronics.com/pages/contact-us

-----------------------------------------------------------------------
### CHANGE LOG ###

2020.03.27 v1.3
- changed the structure of the OBD2 DBC back to only support PIDs from CAN ID 7E8 to keep things simple as 7E8 typically contains the relevant data.
  If use cases require the other CAN IDs, please contact us.

2020.02.24 v1.2
- added CAN IDs 7E9-7EF as per the OBD2 standard

2020.10.22 v1.4
- fixed PID 5C to use degC as unit

2021.10.12 v1.4 Extended
- added separate OBD2 DBC for extended identifiers