# CoralProto
A fast, binary and garbage-free serialization framework with a simple schema definition language.

## Features
- Simple schema definition language with message type and subtype
- Fast parsing (or direct access without parsing)
- Strictly binary (big-endian)
- Ascii encoding for logging/debugging
- Garbage Free (no GC overhead)
- Primary Types (boolean, char, byte, short, int, long, float and double)
- Fixed Byte and Char Arrays
- Variable Byte and Char Arrays (VarChars and VarBytes)
- Enum Fields (CharEnum, ShortEnum, IntEnum and TwoCharEnum)
- Fields can be made optional
- Repeating Groups with nesting support (repeating groups inside repeating groups)
- Schema evolution by appending new (optional or non-optional) fields

  
