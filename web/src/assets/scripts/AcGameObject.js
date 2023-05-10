const AC_GAME_OBJECT = []

export class AcGameObject {
  constructor() {
    AC_GAME_OBJECT.push(this)
    this.timeDelta = 0
    this.hasCalledStart = false
  }

  // 只执行一次
  start() {}

  // 每一帧执行一次，除了第一帧以外
  update() {}

  // 删除之前执行
  onDestroy() {}

  // 删除执行
  destroy() {
    this.onDestroy()

    for (let i in AC_GAME_OBJECT) {
      const obj = AC_GAME_OBJECT[i]
      if (obj === this) {
        AC_GAME_OBJECT.splice(i) // 删除当前对象
        break
      }
    }
  }
}

let lastTimeStamp // 上一次执行时刻

const step = (timeStamp) => {
  for (let obj of AC_GAME_OBJECT) {
    if (!obj.hasCalledStart) {
      obj.hasCalledStart = true
      obj.start()
    } else {
      obj.timeDelta = timeStamp - lastTimeStamp // 当前执行时刻 - 上一次执行时刻
      obj.update()
    }
  }

  lastTimeStamp = timeStamp
  requestAnimationFrame(step)
}

requestAnimationFrame(step)