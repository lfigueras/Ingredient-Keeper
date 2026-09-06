package com.lovely.bakingrecipes.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Pastry::class, Ingredient::class, Step::class],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PastryDatabase : RoomDatabase() {

    abstract fun pastryDao(): PastryDao

    companion object {

        // v1 -> v2: introduced the ingredients table with a cascade FK to pastries.
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `ingredients` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`pastryId` INTEGER NOT NULL, " +
                        "`name` TEXT NOT NULL, " +
                        "`amount` REAL NOT NULL, " +
                        "`unit` TEXT NOT NULL, " +
                        "FOREIGN KEY(`pastryId`) REFERENCES `pastries`(`id`) " +
                        "ON UPDATE NO ACTION ON DELETE CASCADE )"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_ingredients_pastryId` " +
                        "ON `ingredients` (`pastryId`)"
                )
            }
        }

        // v2 -> v3: added created/updated timestamps to pastries.
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `pastries` ADD COLUMN `createdAt` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `pastries` ADD COLUMN `updatedAt` INTEGER NOT NULL DEFAULT 0")
            }
        }

        // v3 -> v4: introduced the steps table with a cascade FK to pastries.
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `steps` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`pastryId` INTEGER NOT NULL, " +
                        "`position` INTEGER NOT NULL, " +
                        "`instruction` TEXT NOT NULL, " +
                        "FOREIGN KEY(`pastryId`) REFERENCES `pastries`(`id`) " +
                        "ON UPDATE NO ACTION ON DELETE CASCADE )"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_steps_pastryId` " +
                        "ON `steps` (`pastryId`)"
                )
            }
        }

        // v4 -> v5: added recipe metadata (servings, times, difficulty).
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `pastries` ADD COLUMN `servings` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `pastries` ADD COLUMN `prepMinutes` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `pastries` ADD COLUMN `cookMinutes` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `pastries` ADD COLUMN `difficulty` TEXT NOT NULL DEFAULT ''")
            }
        }

        @Volatile
        private var INSTANCE: PastryDatabase? = null

        fun getDatabase(context: Context): PastryDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PastryDatabase::class.java,
                    "baking_recipes_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .build()

                INSTANCE = instance

                instance
            }
        }
    }
}