import { AcGameObject } from './AcGameObject' // {} 解构赋值，如果导入文件是 export default 导出则不需要解构，因为全部都导出来了

export class GameMap extends AcGameObject {
  constructor(ctx, parent) {
    super()

    this.ctx = ctx
    this.parent = parent
    this.absoluteDistance = 0 // 一个单位的长度

    this.rows = 13
    this.cols = 13
  }

  start() {}

  updateSize() {
    this.absoluteDistance = Math.min(
      this.parent.clientWidth / this.cols,
      this.parent.clientHeight / this.rows
    )

    this.ctx.canvas.width = this.absoluteDistance * this.cols
    this.ctx.canvas.height = this.absoluteDistance * this.rows
  }

  update() {
    // 特别注意：这俩方法顺序不能错
    this.updateSize() // 更新长度
    this.render() // 渲染
  }

  // 渲染函数
  render() {
    const colorEven = '#AAD752', colorOdd = '#A2D048'

    for (let r = 0; r < this.rows; r ++ ) {
        for (let c = 0; c < this.cols; c ++ ) {
            if ((r + c) % 2 == 0) {
                this.ctx.fillStyle = colorEven;
            } else {
                this.ctx.fillStyle = colorOdd;
            }

            this.ctx.fillRect(c * this.absoluteDistance, r * this.absoluteDistance, this.absoluteDistance, this.absoluteDistance)
        }
    }
  }
}
