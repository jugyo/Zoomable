# Zoomable

Zoomable UI component for Jetpack Compose.

## Examples

Basic:

```kotlin
@OptIn(ExperimentalPagerApi::class)
@Composable
fun Screen() {
    val pagerState = rememberPagerState(initialPage = 0)

    val images = listOf(
        R.drawable.sample1,
        R.drawable.sample2,
        R.drawable.sample3,
        R.drawable.sample4,
    )

    val zoomableStates = List(images.size) { index ->
        rememberBasicZoomableState(index)
    }

    HorizontalPager(
        count = images.size,
        state = pagerState,
        userScrollEnabled = !zoomableStates[pagerState.currentPage].isZooming
    ) { index ->
        Box(modifier = Modifier.fillMaxSize()) {
            Zoomable(
                modifier = Modifier
                    .aspectRatio(1f)
                    .align(Alignment.Center),
                state = zoomableStates[index],
            ) {
                Image(
                    painter = painterResource(images[index]),
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.LightGray),
                    contentDescription = "Photo 1"
                )
            }
        }
    }
}
```


<img src="https://github.com/jugyo/Zoomable/blob/main/screenshots/BasicZoomableSample.gif?raw=true" width="460">

Instagram Like:

```kotlin
@Composable
fun Screen() {
    val listState = rememberLazyListState()
    val images = listOf(
        R.drawable.sample1,
        R.drawable.sample2,
        R.drawable.sample3,
        R.drawable.sample4,
    )
    val zoomableStates = List(images.size) { index ->
        rememberInstagramLikeZoomableState(index)
    }

    MaterialTheme {
        LazyColumn(
            state = listState,
            userScrollEnabled = zoomableStates.none { it.isZooming }
        ) {
            itemsIndexed(images) { index, item ->
                Zoomable(
                    state = zoomableStates[index],
                    modifier = Modifier
                        .padding(20.dp)
                        .zIndex(if (zoomableStates[index].isZooming) 1f else 0f)
                        .aspectRatio(1f)
                ) {
                    Image(
                        painter = painterResource(item),
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.LightGray),
                        contentDescription = "Photo 1"
                    )
                }
            }
        }
    }
}

```

<img src="https://github.com/jugyo/Zoomable/blob/main/screenshots/InstagramLikeZoomableSample.gif?raw=true" width="460">
