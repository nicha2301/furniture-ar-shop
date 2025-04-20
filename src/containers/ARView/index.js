import React, { Component } from 'react';
import { StyleSheet, View, TouchableOpacity, Text } from 'react-native';

import {
  ViroARScene,
  ViroText,
  ViroMaterials,
  ViroARSceneNavigator,
  Viro3DObject,
  ViroAmbientLight,
  ViroSpotLight,
  ViroNode,
  ViroQuad,
  ViroBox,
} from "@reactvision/react-viro";

// Khởi tạo vật liệu với nhiều màu
ViroMaterials.createMaterials({
  product_material_brown: {
    diffuseColor: '#8B4513',
    lightingModel: "PBR",
  },
  product_material_black: {
    diffuseColor: '#333333',
    lightingModel: "PBR",
  },
  product_material_white: {
    diffuseColor: '#EEEEEE',
    lightingModel: "PBR",
  },
});

// Props cho ARScene
const ARScene = (props) => {
  const { modelScale, productColor, rotation, onLoadStart, onLoadEnd, onError } = props;
  const finalProductScale = [modelScale * 0.4, modelScale * 0.4, modelScale * 0.4];
  const [modelLoaded, setModelLoaded] = React.useState(false);
  const [modelError, setModelError] = React.useState(false);

  const handleLoadStart = () => {
    setModelLoaded(false);
    if (onLoadStart) onLoadStart();
  };

  const handleLoadEnd = () => {
    setModelLoaded(true);
    if (onLoadEnd) onLoadEnd();
  };

  const handleError = (error) => {
    setModelError(true);
    if (onError) onError(error);
  };

  return (
    <ViroARScene>
      <ViroAmbientLight color="#ffffff" intensity={200} />

      {!modelLoaded && !modelError && (
        <ViroText
          text="Đang tải mô hình 3D..."
          scale={[0.3, 0.3, 0.3]}
          position={[0, 0.5, -1]}
          style={{ fontSize: 30, fontFamily: 'Arial', color: 'white', textAlignVertical: 'center', textAlign: 'center' }}
        />
      )}

      <ViroNode position={[0, -1.5, -2]} dragType="FixedToWorld" onDrag={() => { }}>
        {!modelError ? (
          <Viro3DObject
            source={{ uri: 'https://github.com/viromedia/viro/blob/master/code-samples/js/ARCarDemo/res/tesla/object_car.obj' }}
            resources={[{ uri: 'https://github.com/viromedia/viro/blob/master/code-samples/js/ARCarDemo/res/tesla/object_car.mtl' }]}
            highAccuracyEvents={true}
            position={[0, 0, 0]}
            scale={finalProductScale}
            rotation={[0, rotation, 0]}
            type="OBJ"
            materials={[productColor]}
            onLoadStart={handleLoadStart}
            onLoadEnd={handleLoadEnd}
            onError={handleError}
          />
        ) : (
          // Fallback khi model không tải được
          <ViroBox
            height={0.5}
            width={0.5}
            length={0.5}
            scale={[modelScale, modelScale, modelScale]}
            rotation={[0, rotation, 0]}
            position={[0, 0, 0]}
            materials={[productColor]}
          />
        )}

        <ViroSpotLight
          innerAngle={5}
          outerAngle={90}
          direction={[0, -1, -0.2]}
          position={[0, 3, 0]}
          color="#ffffff"
          castsShadow={true}
          shadowMapSize={2048}
          shadowNearZ={2}
          shadowFarZ={7}
          shadowOpacity={0.7}
        />

        <ViroQuad
          rotation={[-90, 0, 0]}
          position={[0, -0.001, 0]}
          width={2.5}
          height={2.5}
          arShadowReceiver={true}
        />
      </ViroNode>
    </ViroARScene>
  );
};

// Props cho ARView
const ARView = (props) => {
  const { navigation } = props;
  const [modelScale, setModelScale] = React.useState(0.5);
  const [productColor, setProductColor] = React.useState('product_material_brown');
  const [showControls, setShowControls] = React.useState(false);
  const [rotation, setRotation] = React.useState(0);
  const [message, setMessage] = React.useState('Đang khởi tạo AR...');

  const _onLoadStart = () => {
    setMessage('Đang tải mô hình 3D...');
  };

  const _onLoadEnd = () => {
    setMessage('Mô hình đã tải!');
  };

  const _onError = (error) => {
    console.log('Lỗi tải mô hình:', error);
    setMessage('Lỗi tải mô hình. Vui lòng thử lại.');
  };

  const _zoomIn = () => {
    setModelScale(Math.min(modelScale + 0.1, 1.5));
    setMessage(`Phóng to (${(modelScale + 0.1).toFixed(1)}x)`);
  };

  const _zoomOut = () => {
    setModelScale(Math.max(modelScale - 0.1, 0.1));
    setMessage(`Thu nhỏ (${(modelScale - 0.1).toFixed(1)}x)`);
  };

  const _changeProductColor = (color) => {
    setProductColor(color);
    setMessage('Đã đổi màu sản phẩm');
  };

  const _toggleControls = () => {
    setShowControls(!showControls);
  };

  const _rotateLeft = () => {
    setRotation(rotation - 45);
    setMessage('Xoay trái');
  };

  const _rotateRight = () => {
    setRotation(rotation + 45);
    setMessage('Xoay phải');
  };

  // UI điều khiển
  const ControlPanel = () => {
    if (!showControls) {
      return (
        <View style={styles.arControlsContainer}>
          <TouchableOpacity style={styles.showControlsButton} onPress={_toggleControls}>
            <Text style={styles.buttonText}>Hiện điều khiển</Text>
          </TouchableOpacity>
        </View>
      );
    }

    return (
      <View style={styles.arControlsContainer}>
        <View style={styles.controlRow}>
          <TouchableOpacity style={styles.controlButton} onPress={_zoomIn}>
            <Text style={styles.buttonText}>+</Text>
          </TouchableOpacity>
          <TouchableOpacity style={styles.controlButton} onPress={_zoomOut}>
            <Text style={styles.buttonText}>-</Text>
          </TouchableOpacity>
        </View>
        <View style={styles.controlRow}>
          <TouchableOpacity style={styles.controlButton} onPress={_rotateLeft}>
            <Text style={styles.buttonText}>↺</Text>
          </TouchableOpacity>
          <TouchableOpacity style={styles.controlButton} onPress={_rotateRight}>
            <Text style={styles.buttonText}>↻</Text>
          </TouchableOpacity>
        </View>
        <View style={styles.controlRow}>
          <TouchableOpacity 
            style={[styles.colorButton, { backgroundColor: '#8B4513' }]} 
            onPress={() => _changeProductColor('product_material_brown')}
          />
          <TouchableOpacity 
            style={[styles.colorButton, { backgroundColor: '#333333' }]} 
            onPress={() => _changeProductColor('product_material_black')}
          />
          <TouchableOpacity 
            style={[styles.colorButton, { backgroundColor: '#EEEEEE' }]} 
            onPress={() => _changeProductColor('product_material_white')}
          />
        </View>
        <TouchableOpacity style={styles.hideControlsButton} onPress={_toggleControls}>
          <Text style={styles.buttonText}>Ẩn điều khiển</Text>
        </TouchableOpacity>
      </View>
    );
  };

  return (
    <View style={styles.container}>
      <ViroARSceneNavigator
        autofocus={true}
        initialScene={{
          scene: () => (
            <ARScene
              modelScale={modelScale}
              productColor={productColor}
              rotation={rotation}
              onLoadStart={_onLoadStart}
              onLoadEnd={_onLoadEnd}
              onError={_onError}
            />
          ),
        }}
        style={styles.arView}
      />
      <ControlPanel />
      <TouchableOpacity style={styles.backButton} onPress={() => navigation.goBack()}>
        <Text style={styles.backButtonText}>Quay lại</Text>
      </TouchableOpacity>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  arView: {
    flex: 1,
  },
  arControlsContainer: {
    position: 'absolute',
    bottom: 30,
    left: 0,
    right: 0,
    alignItems: 'center',
    justifyContent: 'center',
    padding: 20,
  },
  controlRow: {
    flexDirection: 'row',
    justifyContent: 'center',
    marginBottom: 10,
  },
  controlButton: {
    backgroundColor: 'rgba(0, 0, 0, 0.7)',
    borderRadius: 30,
    width: 60,
    height: 60,
    alignItems: 'center',
    justifyContent: 'center',
    marginHorizontal: 10,
  },
  colorButton: {
    width: 40,
    height: 40,
    borderRadius: 20,
    marginHorizontal: 10,
    borderWidth: 2,
    borderColor: 'white',
  },
  buttonText: {
    color: 'white',
    fontSize: 24,
    fontWeight: 'bold',
  },
  showControlsButton: {
    backgroundColor: 'rgba(0, 0, 0, 0.7)',
    padding: 10,
    borderRadius: 5,
  },
  hideControlsButton: {
    backgroundColor: 'rgba(0, 0, 0, 0.7)',
    padding: 10,
    borderRadius: 5,
    marginTop: 10,
  },
  backButton: {
    position: 'absolute',
    top: 40,
    left: 20,
    backgroundColor: 'rgba(0, 0, 0, 0.7)',
    padding: 10,
    borderRadius: 5,
  },
  backButtonText: {
    color: 'white',
    fontSize: 16,
  },
});

export default ARView; 