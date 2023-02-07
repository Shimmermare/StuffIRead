package com.shimmermare.stuffiread.data

import com.shimmermare.stuffiread.data.stories.Story
import com.shimmermare.stuffiread.data.stories.StoryDatasource
import com.shimmermare.stuffiread.data.stories.StoryDatasourceImpl
import com.shimmermare.stuffiread.data.stories.StoryFile
import com.shimmermare.stuffiread.data.tags.TagCategoryDatasource
import com.shimmermare.stuffiread.data.tags.TagCategoryDatasourceImpl
import com.shimmermare.stuffiread.data.tags.TagDatasource
import com.shimmermare.stuffiread.data.tags.TagDatasourceImpl
import com.shimmermare.stuffiread.data.util.OffsetDateTimeColumnAdapter
import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import java.nio.file.Path
import java.util.*
import kotlin.io.path.absolute

class StoryDatabase(
    file: Path
) : AutoCloseable {
    val driver: SqlDriver
    val database: Database

    val storyDatasource: StoryDatasource
    val tagCategoryDatasource: TagCategoryDatasource
    val tagDatasource: TagDatasource

    init {
        driver = JdbcSqliteDriver("jdbc:sqlite:${file.absolute()}", Properties())
        database = Database.invoke(
            driver,
            storyAdapter = Story.Adapter(
                createdTsAdapter = OffsetDateTimeColumnAdapter,
                updatedTsAdapter = OffsetDateTimeColumnAdapter,
                firstReadTsAdapter = OffsetDateTimeColumnAdapter,
                lastReadTsAdapter = OffsetDateTimeColumnAdapter
            ),
            storyFileAdapter = StoryFile.Adapter(
                formatAdapter = EnumColumnAdapter()
            )
        )

        storyDatasource = StoryDatasourceImpl(database)
        tagCategoryDatasource = TagCategoryDatasourceImpl(database)
        tagDatasource = TagDatasourceImpl(database)

        handleFirstRunOrMigrations()
    }

    private fun handleFirstRunOrMigrations() = database.transaction {
        val currentVer = getVersion()
        if (currentVer == 0) {
            Database.Schema.create(driver)
            setVersion(Database.Schema.version)

            val defaultDataInitializer = DefaultDataInitializer(
                database,
                tagCategoryDatasource,
                tagDatasource
            )
            defaultDataInitializer.initDefaultData()
        } else {
            val schemaVer = Database.Schema.version
            if (schemaVer > currentVer) {
                Database.Schema.migrate(driver, currentVer, schemaVer)
                setVersion(schemaVer)
            }
        }
    }

    private fun getVersion(): Int = driver.executeQuery(null, "PRAGMA user_version;", 0, null)
        .use { it.getLong(0)!!.toInt() }

    private fun setVersion(version: Int) = driver.execute(null, "PRAGMA user_version = $version;", 0, null)

    override fun close() {
        driver.close()
    }

    companion object {
    }
}