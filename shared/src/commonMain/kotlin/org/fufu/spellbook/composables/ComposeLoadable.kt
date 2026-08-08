package org.fufu.spellbook.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

data class ComposeLoadable<T>(
    val concreteState: T? = null,
    val loading: Boolean = concreteState == null
){
    @Composable
    fun ifLoaded(content: @Composable (T) -> Unit){
        if(!loading && concreteState != null){
            content(concreteState)
        }
    }

    @Composable
    fun ifNotLoaded(content: @Composable () -> Unit){
        if(loading){
            content()
        }
    }

    @Composable
    fun ifLoadedNull(action: () -> Unit){
        if(!loading && concreteState == null){
            action()
        }
    }

    fun <R>combine(other: ComposeLoadable<R>): ComposeLoadable<Pair<T,R>> {
        if(
            this.loading || this.concreteState == null
            || other.loading || other.concreteState == null){
            return ComposeLoadable()
        }
        return ComposeLoadable(Pair(this.concreteState, other.concreteState))
    }

    @Composable
    fun map(
        ifNotLoaded: @Composable () -> Unit,
        ifLoadedNull: () -> Unit = {},
        ifLoaded: @Composable (T) -> Unit,
    ){
        this.ifLoaded(ifLoaded)
        this.ifNotLoaded(ifNotLoaded)
        this.ifLoadedNull(ifLoadedNull)
    }
}

@Composable
fun <T> withFullScreenLoading(loadable: ComposeLoadable<T>, content: @Composable (T) -> Unit){
    loadable.map(
        ifNotLoaded = {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        }
    ){
        content(it)
    }
}