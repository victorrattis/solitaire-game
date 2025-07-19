
attribute vec4 aPosition;
attribute vec2 aTexCoord;

varying vec2 vUv;

uniform mat4 mvp;

void main() {
    gl_Position = mvp * aPosition;
    vUv = aTexCoord;
}
