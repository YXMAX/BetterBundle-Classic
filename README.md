# BetterBundle
---
BetterBundle is a multifunctional bundle plugin that expands players' backpacks to improve their gaming experience and enhance their efficiency in all aspects. It can be used to take various items with you
Items and weapons needed
The plugin uses the new Lz4 compression algorithm to compress various warehouse data stored in Base64, which is secure, efficient, and reduces space consumption
---
### Plugin dependencies
- Vault [Required]
- PlaceHolderAPI (optional)
---
### Main functions
- Bundle storage
  - The warehouse contents can be opened by right clicking on the head of the storage bag, and it also supports opening the storage bag with the left hand
  - The storage bag warehouse content is encrypted using Base64 encoding to ensure content security
  - Base64 encoding is encrypted and compressed using the Lz4 compression algorithm, greatly reducing the occupied space and byte length
  - When others open the same storage bag, it will display as occupied, ensuring absolute safety of the contents
  - The storage bag also has an automatic sorting function to automatically organize messy items
  - The storage bag uses NBT grade Customs Tag to store ID identification numbers, which are extremely difficult to confuse (1.13+)
  - Strict scanning of Lore by storage bag (1.12)
- Bundle management [PREMIUM]
  - Each storage bag has its own management interface to manage talents and backpack capacity
  - Can intuitively observe the status and various information of the storage bag
- Bundle upgrade
  - The upgradable storage bag can expand its internal capacity up to the customized maximum capacity
  - There are two modes to choose from for the required upgrade points, ensuring diversity
  - Points are given by instructions for backend operations that are compatible with various menus
- Bundle Talent [PREMIUM]
  - Each storage bag has different usage skill licenses
  - The storage bag has 6 skills: fishing storage, mineral storage, crop storage, monster drop storage, death binding block storage
  - Customizable points required for each skill under different permission groups
  - Talent can be enabled/disabled through configuration file settings
  - The prompt information of talent supports localized language display, and the display names of stored items will be consistent with the client language
- Bundle Blueprint [PREMIUM]
  - Support setting various blueprints, which will be stored in the same normal storage bag
  - Various items can be pre placed in the warehouse. After the blueprint is given, the items will follow the storage bag obtained by the player
  - Random skills can be given and whether they do not require purchase can be set
- Bundle inquiry (OP)
  - Support viewing the contents of storage bags with different IDs and making changes
  - Support querying even when players are offline
- Player Information Query (OP)
  - Support viewing storage bags held by players/offline players
  - Support viewing various statuses and details of storage bags
- MySQL support
  - Support cross server synchronization of warehouse data and player points
