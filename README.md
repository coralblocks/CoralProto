# CoralProto
A fast, binary and garbage-free serialization framework with a simple schema definition language.

## Features
- Simple schema definition language with message type and subtype
- Fast parsing (or direct access without parsing)
- Strictly binary (big-endian)
- Ascii encoding for logging/debugging
- Garbage-free (no GC overhead)
- Primitive types (boolean, char, byte, short, int, long, float and double)
- Fixed byte and char arrays
- Variable byte and char arrays (VarChars and VarBytes)
- Enum fields (CharEnum, ShortEnum, IntEnum and TwoCharEnum)
- Fields can be made optional
- Repeating groups with nesting support (repeating groups inside repeating groups)
- Schema evolution by appending new (optional or non-optional) fields

## Schema Definition Language
```plain
  TYPE = P
  SUBTYPE = C
  
  symbolId: long
  symbolDesc: varchars(128)
  mqReqId: long!
  
  bids:
      levelId: long!
      priceLevel: double
      qty: int
      legs:
        legId: int
        legDesc: chars(8)!
      orders: int
  
  asks:
      levelId: long!
      priceLevel: double
      qty: int
      legs:
        legId: int
        legDesc: chars(8)!
      orders: int
  
  lastTradeQty: long!
  lastTradePrice: double!
```
- TYPE and SUBTYPE are mandatory
- An exclamation mark at the end of a field indicates that the field is optional
- Repeating groups are created through indentation
- The number between parenthesis for varchars (and varbytes) is the maximum allowed size/length
- The number between parenthesis for chars (and bytes) is the fixed size/length

**NOTE:** For convenience, you can place the schema specification inside the Java class so that when you execute its main method the class source code is updated. You can see an example [here](https://github.com/coralblocks/CoralProto/blob/main/src/main/java/com/coralblocks/coralproto/example/PriceChangeMessage.java).
