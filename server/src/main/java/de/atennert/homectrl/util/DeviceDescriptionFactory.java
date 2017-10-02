package de.atennert.homectrl.util;

import de.atennert.homectrl.registration.NodeDescription;
import de.atennert.homectrl.DataType;
import de.atennert.homectrl.controls.AbstractController;
import de.atennert.homectrl.dataprocessing.AbstractDataProcessor;
import de.atennert.homectrl.registration.DataDescription;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.*;

public class DeviceDescriptionFactory
{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private Set<NodeDescription> nodeDescriptions;

    private Map<Integer, DataDescription> dataDescriptions;

    private Set<AbstractController<?>> controls;

    private Set<AbstractDataProcessor<?>> processors;

    private Map<Integer, Object> defaultValues;


    public DeviceDescriptionFactory(String configuration)
    {
        loadConfiguration(configuration);
    }

    private void loadConfiguration(String configuration)
    {
        log.debug("Loading device configuration from " + configuration);

        dataDescriptions = new HashMap<>();
        nodeDescriptions = new HashSet<>();
        processors = new HashSet<>();
        controls = new HashSet<>();
        defaultValues = new HashMap<>();

        Resource resource = new FileSystemResource(configuration);

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null)
            {
                builder.append(line);
            }

            JSONArray rootElem = new JSONArray(builder.toString());

            // get node descriptions
            for (int i=0; i<rootElem.length(); i++)
            {
                JSONObject obj = rootElem.getJSONObject(i);
                if ("node".equals(obj.getString("type")))
                {
                    loadDataDescriptions(obj);
                    loadNodeDescription(obj);
                }
            }

            // get processor descriptions
            for (int i=0; i<rootElem.length(); i++)
            {
                JSONObject obj = rootElem.getJSONObject(i);
                if ("processor".equals(obj.getString("type")))
                {
                    loadProcessor(obj);
                }
            }

            // get control descriptions
            for (int i=0; i<rootElem.length(); i++)
            {
                JSONObject obj = rootElem.getJSONObject(i);
                if ("control".equals(obj.getString("type")))
                {
                    loadControl(obj);
                }
            }
            log.debug("Configuration successfully loaded.");
        }
        catch (Exception e)
        {
            throw new RuntimeException("Unable to load device configuration!", e);
        }
    }

    /**
     * Loads sensors and actors into the data description map.
     * TODO load sensor/actor information and use it to restrict the use
     * @param node representation of a node
     */
    private void loadDataDescriptions(JSONObject node)
    {
        String nodeName = node.getString("name");
        JSONArray devices = node.getJSONArray("devices");

        for (int i=0; i<devices.length(); i++){
            JSONObject device = devices.getJSONObject(i);
            int id = device.getInt("id");
            Object value = getObjectValue(device);

            dataDescriptions.put(id, new DataDescription(
                        id,
                        DataType.getDataTypeFromString(device.getString("data")),
                        nodeName,
                        device.getString("reference"),
                        value));

            defaultValues.put(id, value);
        }
    }

    private static Object getObjectValue(JSONObject obj)
    {
        Object value;
        switch (DataType.getDataTypeFromString(obj.getString("data"))){
            case INTEGER:
                value = new Integer(obj.getInt("value"));
                break;
            case BOOL:
                value = new Boolean(obj.getBoolean("value"));
                break;
            case DOUBLE:
                value = new Double(obj.getDouble("value"));
                break;
            case STRING:
                value = new String(obj.getString("value"));
                break;
            default:
                throw new UnsupportedOperationException();
                // TODO read date from device configuration
        }
        return value;
    }

    /**
     * Adds the data for the given node into the node descriptions map.
     * @param node
     */
    private void loadNodeDescription(JSONObject node)
    {
        NodeDescription nodeDescription = new NodeDescription(node.getString("name"));
        JSONArray sendAddresses = node.getJSONArray("sendAddresses");
        JSONArray receiveAddresses = node.getJSONArray("receiveAddresses");
        JSONArray interpreters = node.getJSONArray("interpreters");

        for (int i=0; i<sendAddresses.length(); i++)
        {
            nodeDescription.addSendAddress(
                    AddressFactory.getAddress(
                            mapAddressData(sendAddresses.getJSONObject(i))));
        }
        for (int i=0; i<receiveAddresses.length(); i++)
        {
            nodeDescription.addReceiveAddress(
                    AddressFactory.getAddress(
                            mapAddressData(receiveAddresses.getJSONObject(i))));
        }
        for (int i=0; i<interpreters.length(); i++)
        {
            nodeDescription.addInterpreter(interpreters.getString(i));
        }
        nodeDescriptions.add(nodeDescription);
    }

    private static Map<String, String> mapAddressData(JSONObject addressObj)
    {
        Map<String, String> data = new HashMap<>();
        String[] keys = JSONObject.getNames(addressObj);

        for (String key : keys)
        {
            data.put(key, addressObj.getString(key));
        }

        return data;
    }

    private void loadProcessor(JSONObject processorObj) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        Constructor<?> constructor = Class.forName(processorObj.getString("class")).getConstructors()[0];

        // construct the processor
        Annotation[][] parameterFields = constructor.getParameterAnnotations();
        ArrayList<Object> arguments = new ArrayList<>(parameterFields.length);

        for (int i=0; i<parameterFields.length; i++)
        {
            Annotation[] parameterField = parameterFields[i];
            String fieldId = ((ConfigurationField)parameterField[0]).fieldId();
            if ("resources".equals(fieldId))
            {
                int[] ids = getResourceIds(processorObj.getJSONArray(fieldId));
                Class<?> type = (Class<?>)((ParameterizedType) constructor.getGenericParameterTypes()[i]).getActualTypeArguments()[1];
                if (type.equals(Integer.class))
                {
                    arguments.add(this.<Integer> getResourceValues(ids));
                }
                else if (type.equals(Double.class))
                {
                    arguments.add(this.<Double> getResourceValues(ids));
                }
                else if (type.equals(Boolean.class))
                {
                    arguments.add(this.<Boolean> getResourceValues(ids));
                }
                else if (type.equals(String.class))
                {
                    arguments.add(this.<String> getResourceValues(ids));
                }
                else
                {
                    throw new RuntimeException("Unable to match type!");
                }
            }
            else
            {
                arguments.add(processorObj.get(fieldId));
            }
        }

        defaultValues.put(processorObj.getInt("id"), getObjectValue(processorObj));

        processors.add((AbstractDataProcessor<?>) constructor.newInstance(arguments.toArray(new Object[arguments.size()])));
    }

    private int[] getResourceIds(JSONArray resourcesObj)
    {
        int[] ids = new int[resourcesObj.length()];
        for (int i=0; i<ids.length; i++)
        {
            ids[i] = resourcesObj.getInt(i);
        }
        return ids;
    }

    @SuppressWarnings("unchecked")
    private <T> Map<Integer, T> getResourceValues(int[] ids)
    {
        Map<Integer, T> values = new HashMap<>();

        for (int id : ids)
        {
            values.put(id, (T)defaultValues.get(id));
        }

        return values;
    }

    private void loadControl(JSONObject controlObj) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        Constructor<?> constructor = Class.forName(controlObj.getString("class")).getConstructors()[0];

        // construct the control
        Annotation[][] parameterFields = constructor.getParameterAnnotations();
        ArrayList<Object> arguments = new ArrayList<>(parameterFields.length);

        for (Annotation[] parameterField : parameterFields)
        {
            arguments.add(controlObj.get(((ConfigurationField) parameterField[0]).fieldId()));
        }

        AbstractController<?> control = (AbstractController<?>) constructor.newInstance(arguments.toArray(new Object[arguments.size()]));

        // fill in the default values from the dependencies
        Field[] fields = control.getClass().getFields();
        for (Field field : fields)
        {
            DeviceValue selectorAnnotation = field.getAnnotation(DeviceValue.class);
            if (selectorAnnotation != null)
            {
                field.setAccessible(true);
                field.set(control, defaultValues.get(controlObj.getInt(selectorAnnotation.idSelector())));
            }
        }

        controls.add(control);
    }



    public Set<DataDescription> getDataDescriptions()
    {
        return new HashSet<>(dataDescriptions.values());
    }

    public Set<NodeDescription> getNodeDescriptions()
    {
        return nodeDescriptions;
    }

    public Set<AbstractController<?>> getControls()
    {
        return controls;
    }

    public Set<AbstractDataProcessor<?>> getProcessors()
    {
        return processors;
    }
}
