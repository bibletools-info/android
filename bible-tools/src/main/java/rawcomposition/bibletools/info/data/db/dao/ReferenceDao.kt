package rawcomposition.bibletools.info.data.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import io.reactivex.Flowable
import io.reactivex.Maybe
import rawcomposition.bibletools.info.data.model.Reference

@Dao
interface ReferenceDao : BaseDao<Reference> {

    @Query("SELECT * FROM `references` WHERE shortRef = :query OR textRef = :query")
    fun findReference(query: String): Maybe<Reference>

    @Query("SELECT * FROM `references` WHERE favorite = 1")
    fun listFavorites(): Flowable<List<Reference>>
}