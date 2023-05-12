export class Cells {
  constructor(r, c) {
    this.r = r
    this.c = c
    // 单元格子中心点坐标，注意做一个变换，canvas画布 列是 x，行是 y
    this.x = c + 0.5
    this.y = r + 0.5
  }
}
