package com.example.mirecetario

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        Receta::class,
        Ingrediente::class,
        PasoReceta::class,
        ItemCompra::class,
        MultimediaPaso::class
    ],
    version = 7,
    exportSchema = false
)
abstract class RecetarioDatabase : RoomDatabase() {

    abstract fun recetaDao(): RecetaDao

    abstract fun ingredienteDao(): IngredienteDao

    abstract fun pasoRecetaDao(): PasoRecetaDao

    abstract fun itemCompraDao(): ItemCompraDao

    abstract fun multimediaPasoDao(): MultimediaPasoDao


    companion object {

        @Volatile
        private var INSTANCE: RecetarioDatabase? = null


        /*
         * =====================================================
         * MIGRACIÓN 5 -> 6
         * Crea la tabla de lista de compras
         * =====================================================
         */
        private val MIGRATION_5_6 =
            object : Migration(5, 6) {

                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {

                    database.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS lista_compras (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            descripcion TEXT NOT NULL,
                            comprado INTEGER NOT NULL
                        )
                        """.trimIndent()
                    )
                }
            }


        /*
         * =====================================================
         * MIGRACIÓN 6 -> 7
         * Crea la tabla para múltiples fotos y videos
         * =====================================================
         */
        private val MIGRATION_6_7 =
            object : Migration(6, 7) {

                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {

                    database.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS multimedia_paso (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            pasoId INTEGER NOT NULL,
                            uri TEXT NOT NULL,
                            tipo TEXT NOT NULL,
                            orden INTEGER NOT NULL,
                            FOREIGN KEY(pasoId)
                                REFERENCES pasos_receta(id)
                                ON DELETE CASCADE
                        )
                        """.trimIndent()
                    )


                    database.execSQL(
                        """
                        CREATE INDEX IF NOT EXISTS index_multimedia_paso_pasoId
                        ON multimedia_paso(pasoId)
                        """.trimIndent()
                    )
                }
            }


        /*
         * =====================================================
         * OBTENER BASE DE DATOS
         * =====================================================
         */
        fun obtenerBaseDeDatos(
            context: Context
        ): RecetarioDatabase {

            return INSTANCE ?: synchronized(this) {

                val instancia =

                    Room.databaseBuilder(
                        context.applicationContext,
                        RecetarioDatabase::class.java,
                        "recetario_database"
                    )
                        .addMigrations(
                            MIGRATION_5_6,
                            MIGRATION_6_7
                        )
                        .fallbackToDestructiveMigration(true)
                        .build()


                INSTANCE =
                    instancia


                instancia
            }
        }
    }
}